package org.ignis.javaMud.Mud.derby;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.derby.exception.DBException;
import org.ignis.javaMud.Mud.derby.exception.LoginFailedException;
import org.ignis.javaMud.Mud.derby.exception.UserDeniedException;
import org.ignis.javaMud.utils.DatabaseTools;

public class PlayerDao {
	private static Logger LOG = LogManager.getLogger(PlayerDao.class);
	public static boolean checkUser(Connection con, String userName) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("Select UserID from APP.MUDUSER where userName = ?");
        	DatabaseTools.setString(ps, 1, userName);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
	        }
		} catch (SQLException e) {
			LOG.catching(Level.FATAL, e);
			return false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
		return false;
	}
	public static int login(Connection con, String userName, String password, String ip) throws LoginFailedException,UserDeniedException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rs2 = null;
		PreparedStatement ps2 = null;
		Player ret=null;
		int id;
		try {
			int denied;
			Clob playerC;
			ps = con.prepareStatement("Select UserID,DENIED from APP.MUDUSER where userName = ? and password=?");
			int p=0;
        	DatabaseTools.setString(ps,++p, userName);
        	DatabaseTools.setString(ps,++p, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				p = 0;
				id = DatabaseTools.getInteger(rs, ++p);
				denied = DatabaseTools.getInteger(rs, ++p);
				if(denied >0) {
					throw new UserDeniedException();
				}
			}
			else {
				throw new LoginFailedException();
			}
			rs.close();
			ps.close();
			
			ps2 = con.prepareStatement("update APP.MUDUSER set LASTACCESS = CURRENT_TIMESTAMP(), LASTIP =? where userId = ?");
			p=0;
        	DatabaseTools.setString(ps2,++p, ip);
        	DatabaseTools.setInteger(ps2,++p, id);
			rs2 = ps2.executeQuery();
			
			rs2.close();
			ps2.close();
		} catch (SQLException e) {
			LOG.catching(Level.FATAL, e);
			return 0;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (rs2 != null) {
				try {
					rs2.close();
				} catch (SQLException e) {
				}
			}
			if (ps2 != null) {
				try {
					ps2.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return 0;
		
	}
	public static Reader getPlayer(Connection con, int userID) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			Clob playerC;
			ps = con.prepareStatement("Select player from APP.MUDUSER where userId = ?");
			int p=0;
        	DatabaseTools.setInteger(ps,++p, userID);
			rs = ps.executeQuery();
			if (rs.next()) {
				p = 0;
				playerC = DatabaseTools.getClob(rs, ++p);
				return playerC.getCharacterStream();
			}
			

		} catch (SQLException e) {
			LOG.catching(Level.FATAL, e);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return null;
		
	}
}
