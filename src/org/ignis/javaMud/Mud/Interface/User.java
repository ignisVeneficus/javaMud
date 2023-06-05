package org.ignis.javaMud.Mud.Interface;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.dataholder.cartography.Map;
import org.ignis.javaMud.Mud.deamon.CommandQueue;
import org.ignis.javaMud.Mud.derby.DBHandler;
import org.ignis.javaMud.Mud.derby.PlayerDao;
import org.ignis.javaMud.Mud.derby.exception.LoginFailedException;
import org.ignis.javaMud.Mud.derby.exception.UserDeniedException;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.messages.Event;
import org.ignis.javaMud.messages.Message;
import org.ignis.javaMud.messages.PlayerStatus;
import org.ignis.javaMud.messages.RoomDescription;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * User, vagyis felhasznalo. Interface a Player (jatekos) fele
 * folyamat:
 * nev,
 * jelszo1
 * jelszo2
 * email cim
 * igazi neve
 * faj
 * 
 * 
 * salt: 9a4d0791f9a51ad02c3f764f361e69ca
 * property-bol jojjon.
 * @author Csaba Toth
 *
 */
// apache Derby adatbazissal osszekapcsolni
public class User implements org.ignis.javaMud.server.User {
	static private Logger LOG = LogManager.getLogger(User.class);
	
	private static final String Salt = "9a4d0791f9a51ad02c3f764f361e69ca";
	private Engine engine;
	/**
	 * Kezdeti statusz, nincs semmi, ures user.
	 */
	static final int STATUS_NONE = 0;
	/**
	 * Nev be lett kerve
	 */
	static final int STATUS_NAME = 1;
	/**
	 * karakter van es a jelszot varunk
	 */
	static final int STATUS_NAME_OLD = 2;
	/**
	 * Nev meg van adva, de uj user (nincs ilyen karakter)
	 */
	static final int STATUS_NAME_NEW = 3;
	/**
	 * kiirjuk a jelszo bekero uzenetet
	 */
	static final int STATUS_PASS0 = 4;
	/**
	 * Elso jelszot varjuk
	 */
	static final int STATUS_PASS1 = 5;
	/**
	 * Masodik jelszot varjuk
	 */
	static final int STATUS_PASS2 = 6;
	/**
	 * Emailt varjuk
	 */
	static final int STATUS_EMAIL = 7;
	/**
	 * Igazi nevet varjuk
	 */
	static final int STATUS_REALNAME = 8;
	
	
	/**
	 * Faj ki lett valasztva
	 */
	static final int STATUS_RACE = 10;
	/**
	 * Statusz valasztas
	 */
	static final int STATUS_STAT = 20;
	/**
	 * tuljutott a kivalasztason
	 */
	static final int STATUS_FINAL = 99;
	/**
	 * Jakson JSON konverter
	 */
	static final ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * Session a valaszolgatashoz
	 */
	private WebSocketSession session;
	/**
	 * allapot. az allapotgrafot le kellene irni
	 */
	private int status;
	/**
	 * A user DB-ben levo azonositoja
	 */
	private int userID;
	
	private String userName;
	private String password;
	private String realName;
	private String email;
	
	private Player player;
	/**
	 * Construktor. kell neki a session, letarolja, es azon keresztul kommunikal
	 * feldobja a kezdeti uzenetet, es a nev bekerest
	 */
	public User(WebSocketSession session, Engine e) {
		status = STATUS_NONE;
		this.session = session;
		
		
		Message msg = new Message();
		Event event = new Event();
		
		engine = e;
		event.setText(engine.getLogo());
		msg.setEvent(event);
		sendToUser(msg);
		
		//handleStatus(null);
	}
	/**
	 * session modositas, ha leszakad es ujra kapcsolodik (kell?)
	 * @param session
	 */
	public void setSession(WebSocketSession session) {
		this.session = session;
	}
	/**
	 * uzenetkuldes
	 */
	public void sendToUser(Message msg) {
		try {
			String str =objectMapper.writeValueAsString(msg);
			synchronized(session) {
				session.sendMessage(new TextMessage(str));
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * uzenet fogadas es feldolgozas.
	 * 
	 */
	@Override
	public void processText(String message) {
		message = StringUtil.filterInput(message);
		// echo
		sendToUser(Message.createTextMessage(message));
		/*
		if(status!= STATUS_FINAL) {
			handleStatus(message);
		}
		*/
		if(player!=null) {
			Handler obj = engine.getHandler(CommandQueue.REG_NAME);
			if((obj!=null)&&(obj instanceof CommandQueue)) {
				((CommandQueue)obj).addCommand(player, message);
			}
		}
		
	}
	/**
	 * megszakadas kezelese, hutobe a karaktert
	 */
	@Override
	public void closedConnection() {
		if(player!=null) {
			player.setUser(null);
		}
		player = null;
		
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public RoomDescription getRoom() {
		Room r = (Room)player.getEnvironment();
		return RoomDescription.createOne(r.getRoomDescription(player, true));
	}
	
	public PlayerStatus getPlayerStatus() {
		PlayerStatus ps = new PlayerStatus();
		ps.setActHP(player.getActHP());
		ps.setMaxHP(player.getMaxHP());
		ps.setActSP(player.getActSP());
		ps.setMaxSP(player.getMaxSP());
		
		ps.setActMP(player.getActMP());
		ps.setMaxMP(player.getMaxMP());
		
		return ps;
	}
	
	public void sendRoom() {
		RoomDescription rm = getRoom();
		PlayerStatus ps = getPlayerStatus();
		
		Message msg  =new Message();
		msg.setRoom(rm);
		msg.setStatus(ps);
		Room r = (Room)player.getEnvironment();
		Map m = r.getMap();
		if(m!=null) {
			org.ignis.javaMud.messages.Map map = new org.ignis.javaMud.messages.Map();
			map.addMap(m);
			map.setYouAreHere(r.getMapName());
			msg.setMap(map);
		}
		
		sendToUser(msg);
	}
	public void sendStatus() {
		PlayerStatus ps = getPlayerStatus();

		Message msg  =new Message();
		msg.setStatus(ps);
		
		sendToUser(msg);
	}
	public void sendPlayer() {
		PlayerStatus ps = getPlayerStatus();

		Message msg  =new Message();
		msg.setStatus(ps);
		
		sendToUser(msg);
		
	}
	
	
	private String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 100;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();
         
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(hash);
    }
	private byte[] getSalt() {
		return fromHex(Salt);
		
	}
	private byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
	private String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
	private void handleStatus(String input) {
		Message m = new Message();
		Event e = new Event();
		Handler h;
		boolean reset = false;
		switch(status) {
			case STATUS_NONE:
				email = null;
				password = null;
				userName = null;
				realName = null;
				e.setText("Milyen névvel szeretnél játszani?");
				status = STATUS_NAME;
				break;
			case STATUS_NAME:
				if(StringUtils.isBlank(input)) {
					status = STATUS_NONE;
					reset = true;
					break;
				}
				userName = input;
				h = engine.getHandler(DBHandler.REG_NAME);
				if (h instanceof DBHandler) {
					try {
						DBHandler dbHandler = (DBHandler) h;
						Connection con = dbHandler.getConnection();
						boolean hasUser = PlayerDao.checkUser(con, input);
						if(hasUser) {
							e.setText("Ilyen karakter már van.\nJelszó?");
							status = STATUS_NAME_OLD;
						}
						else {
							e.setText("Biztos, hogy ezzel a névvel akarsz játszani?");
							status = STATUS_NAME_NEW;
						}
					} catch (SQLException e1) {
						LOG.catching(e1);
					}
				}
				break;
			case STATUS_NAME_OLD:
				if(StringUtils.isBlank(input)) {
					status = STATUS_NONE;
					reset=true;
					break;
				}
				h = engine.getHandler(DBHandler.REG_NAME);
				if (h instanceof DBHandler) {
					try {
						DBHandler dbHandler = (DBHandler) h;
						Connection con = dbHandler.getConnection();
						
						String ip = session.getRemoteAddress().getHostString();
						String password = generateStorngPasswordHash(input);
						userID = PlayerDao.login(con, userName, password, ip);
					} catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e1) {
					} catch (LoginFailedException e1) {
						e.setText("Hibás jelszó!");
						status = STATUS_NONE;
						reset=true;
						break;
						
					} catch (UserDeniedException e1) {
						e.setText("Nem léphetsz be!");
						status = STATUS_NONE;
						reset=true;
						break;
					}
				}
				break;
			case STATUS_NAME_NEW:
				if((StringUtils.isBlank(input))
						||(!StringUtil.equalsSecoundString("igen", input))
						||(!StringUtil.equalsSecoundString("i", input))) {
					userName = null;
					status = STATUS_NONE;
					reset=true;
					break;
				}
				status = STATUS_PASS0;
				break;
			case STATUS_PASS0:
				e.setText("Adj meg egy jelszavat.");
				status = STATUS_PASS1;
				break;
			case STATUS_PASS1:
				if(StringUtils.isBlank(input)) {
					status = STATUS_NONE;
					reset=true;
					break;
				}
				password = input;
				e.setText("Add meg újra a jelszavad.");
				status = STATUS_PASS2;
				break;
			case STATUS_PASS2:
				if(StringUtils.isBlank(input)) {
					status = STATUS_PASS0;
					reset=true;
					break;
				}
				if(!password.equals(input)) {
					e.setText("A két jelszó nem egyezik!");
					status = STATUS_PASS0;
					reset=true;
					break;
				}
				e.setText("Add meg az email címed! (kötelező)\n(Senkinek nem adjuk oda)");
				status = STATUS_EMAIL;
				break;
			case STATUS_EMAIL:
				if(StringUtils.isBlank(input)) {
					status = STATUS_NONE;
					reset=true;
					break;
				}
				email = input;
				e.setText("Add meg az igazi nevedet (nem kötelező)!");
				status = STATUS_REALNAME;
				break;
			case STATUS_REALNAME:
				if(StringUtils.isBlank(input)) {
					input = "";
				}
				realName = input;
				
				
		}
		if(e.getText().length>0) {
			m.setEvent(e);
			sendToUser(m);
		}
		if(reset) {
			handleStatus(null);
		}
	}
}
