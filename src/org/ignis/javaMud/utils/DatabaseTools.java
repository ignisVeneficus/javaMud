package org.ignis.javaMud.utils;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseTools {
	private static SimpleDateFormat sdf = new SimpleDateFormat(
		    "yyyy-MM-dd HH:mm:ss.S X");
	
	private static Logger LOG = LogManager.getLogger(DatabaseTools.class);
	public static void setString(PreparedStatement aPS, int aIndex,
			String aValue) throws SQLException {
		if(LOG.isTraceEnabled())
			LOG.trace("Insert String: >"+aValue + "< into: " + aPS + " @" + aIndex);
		if (aValue == null)
			aPS.setNull(aIndex, Types.VARCHAR);
		else
			aPS.setString(aIndex, aValue.trim());
	}

	public static Integer getInteger(ResultSet aRS, int aIndex)
			throws SQLException {
		int v;
		boolean wasNull;
		v = aRS.getInt(aIndex);
		wasNull = aRS.wasNull();
		Integer value;
		if (wasNull)
			value= null;
		else
			value= new Integer(v);
		if(LOG.isTraceEnabled())
			LOG.trace("Retrieve Integer: "+value + " from: " + aRS + " @" + aIndex);
		return value;
	}
	public static void setInteger(PreparedStatement aPS, int aIndex,
			Integer aValue) throws SQLException {
		if(LOG.isTraceEnabled())
			LOG.trace("Insert Integer: "+aValue + " into: " + aPS + " @" + aIndex);

		if (aValue == null) {
			aPS.setNull(aIndex, Types.INTEGER);
		} else {
			aPS.setInt(aIndex, aValue.intValue());
		}
	}

	public static Clob getClob(ResultSet aRS, int aIndex)
			throws SQLException {
		Clob ret = (java.sql.Clob)aRS.getObject(aIndex);
			LOG.trace("Retrieve Clob from: " + aRS + " @" + aIndex);
		return ret;
	}
	
}
