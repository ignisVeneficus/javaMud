package org.ignis.javaMud.Mud.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.handlers.Handler;

public class DBHandler implements Handler{
	static private Logger LOG = LogManager.getLogger(DBHandler.class);
	static public final String REG_NAME = "derbyHandler";
	private String dbURL;

	@Override
	public void init(Engine e) {
		String path = e.getProperty("Player.db.path");
		if(StringUtils.isBlank(path)) return;
		path = e.compileRef(path);
		String realPath = e.getRealPath(path).replace("\\", "/");
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	        dbURL = "jdbc:derby:" + realPath + ";create=true";
	        LOG.info("CONFIG: derby connection: " + dbURL);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Connection con = getConnection();
			con.close();
		}
		catch(Exception e1) {
			LOG.catching(Level.FATAL, e1);
		}
		LOG.info("CONFIG: derby started");
	}
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(dbURL); 
	}
	@Override
	public void dest() {
		try {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
