package org.ignis.javaMud.Mud.data;
/**
 * Koordinata az outlands hasznalatahoz
 * @author Csaba Toth (csaba.toth@sptech.ch)
 *
 */
public class Coordinate {
	public static final int DIR_NONE = 0;
	public static final int DIR_E 	= 1;
	public static final int DIR_EK	= 2;
	public static final int DIR_K	= 3;
	public static final int DIR_DK	= 4;
	public static final int DIR_D	= 5;
	public static final int DIR_DNY	= 6;
	public static final int DIR_NY 	= 7;
	public static final int DIR_ENY	= 8;
	
	/**
	 * X eleme
	 */
	private int x;
	/**
	 * Y eleme
	 */
	private int y;
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	/**
	 * koordinata kiszamolasa mozgashoz
	 * @param direction az irany
	 * @return az uj koordinata
	 */
	public Coordinate move(int direction) {
		switch(direction){
		case DIR_E:
			return new Coordinate(x, y-1);
		case DIR_EK:
			return new Coordinate(x+1, y-1);
		case DIR_K:
			return new Coordinate(x+1, y);
		case DIR_DK:
			return new Coordinate(x+1, y+1);
		case DIR_D:
			return new Coordinate(x, y+1);
		case DIR_DNY:
			return new Coordinate(x-1, y+1);
		case DIR_NY:
			return new Coordinate(x-1, y);
		case DIR_ENY:
			return new Coordinate(x-1, y-1);
		}
		return this;
	}
	@Override
	public String toString() {
		return "[" + x + "|" + y + "]";
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Coordinate) {
			Coordinate c = (Coordinate)obj;
			return (x==c.getX())&&(y==c.getY());
		}
		return super.equals(obj);
	}
	static public int getOpposite(int direction) {
		switch(direction){
		case DIR_E:
			return DIR_D;
		case DIR_EK:
			return DIR_DNY;
		case DIR_K:
			return DIR_NY;
		case DIR_DK:
			return DIR_ENY;
		case DIR_D:
			return DIR_E;
		case DIR_DNY:
			return DIR_EK;
		case DIR_NY:
			return DIR_K;
		case DIR_ENY:
			return DIR_DK;
		}
		return DIR_NONE;
		
	}
	
}
