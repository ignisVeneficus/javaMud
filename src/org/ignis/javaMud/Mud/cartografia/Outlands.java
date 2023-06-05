package org.ignis.javaMud.Mud.cartografia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.data.Coordinate;
import org.ignis.javaMud.Mud.dataholder.cartography.Map;
import org.ignis.javaMud.Mud.dataholder.cartography.MapItem;
import org.ignis.javaMud.Mud.dataholder.outlands.Plane;


public class Outlands {
	static final private int R = 8;
	static final private int REC = 2*R+1;
	static private Logger LOG = LogManager.getLogger(Outlands.class);
	static Outlands o = new Outlands();
	private class HeightData{
		private int x;
		private int y;
		private int h;
		private double deg;
		HeightData(int x, int y, int h, int cx, int cy,int ch) {
			double t = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy));
			deg = t==0?0:(double)(h-ch)/t;
			LOG.trace("x,y,h: " +x+"|"+y+ "|"+h+" cx,cy,ch: " + cx + "|"+cy+"|"+ch + " t: "+t + " d: "+deg );
			this.x = x;
			this.y = y;
			this.h = h;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public int getH() {
			return h;
		}
		public double getDeg() {
			return deg;
		}
		public String toString() {
			return "[" + x + "|" + y + "|" + h +"] / " + deg;
		}
	}
	
	
	private static final int HEIGHT_UNDEF = -1;
	private static final int HEIGHT_OUT = -2;
	private static final int HEIGHT_CANTSEE = -3;
	
	public static Map getMap(Coordinate coord, Plane p) {
		int[][] bitmap = new int[REC][REC];
		// types
		@SuppressWarnings("unchecked")
		HashSet<String>[][] items = new HashSet[REC+2][REC+2];
		LinkedHashSet<String> linekeys = p.getLineKeys();
		// van-e ott vonal
		boolean[][][] lineown = new boolean[REC+2][REC+2][linekeys.size()];
		// olyan tipus-e ahova megy vonal
		boolean[][][] linetype = new boolean[REC+2][REC+2][linekeys.size()];
		
		
		LOG.trace(coord);
		
		// kiutjuk a terkepet, hatha lelogunk rola
		for(int i=0;i<REC;i++) {
			for(int j=0;j<REC;j++) {
				bitmap[i][j] = HEIGHT_UNDEF;
			}
		}
		
		// osszeszedjuk a tipusokat, korben 1-el nagyobb blokkal dolgozunk, hogy a folyok/utak kifolyasat is kezelni tudjuk
		for(int i=0;i<REC+2;i++) {
			for(int j=0;j<REC+2;j++) {
				Coordinate c = fromBitmap(coord, i, j, R+1);
				HashSet<String> t = p.getAllType(c);
				items[i][j] = t;
				int pos = 0;
				for(String key:linekeys) {
					lineown[i][j][pos] = t.contains(key);
					linetype[i][j][pos] = p.isLine(key, t, c);
					pos++;
				}
			}
		}
		// kiszamoljuk, hogy az adott koordinatan hany, es milyen iranyba mennek a vonalak
		String[][][] linestrings = new String[REC][REC][linekeys.size()];
		for(int i=0;i<REC;i++) {
			for(int j=0;j<REC;j++) {
				// vonaltipunkent
				for(int k=0;k<linekeys.size();k++) {
					// csak akkor ha van ott tenylegesen vonal (kizarva a tengert)
					if(lineown[i+1][j+1][k]) {
						Coordinate linesCoord = new Coordinate(i+1, j+1);
						StringBuffer b = new StringBuffer();
						for(int ii=1;ii<9;ii++) {
							Coordinate nc = linesCoord.move(ii);
							if(linetype[nc.getX()][nc.getY()][k]) {
								b.append(ii);
							}
						}
						linestrings[i][j][k] = b.toString();
					}
				}
			}
		}
		
		// magassag kezelese
		bitmap[R][R] = p.getTerrainHeight(coord);
		for(int i=0;i<R+1;i++) {
			putHeight(0, R-i, coord,p, bitmap);
			putHeight(0, R+i, coord,p, bitmap);
			putHeight(REC-1, R-i, coord,p, bitmap);
			putHeight(REC-1, R+i, coord,p, bitmap);
			putHeight(R-i,0, coord,p, bitmap);
			putHeight(R+i,0, coord,p, bitmap);
			putHeight(R-i,REC-1, coord,p, bitmap);
			putHeight(R+i,REC-1, coord,p, bitmap);
		}
		
		Map ret = new Map();
		//osszerakjuk az egeszet:
		for(int i=0;i<REC;i++) {
			for(int j=0;j<REC;j++) {
				if(bitmap[i][j]>=0) {
					Coordinate c = fromBitmap(coord, i, j);
					String id = org.ignis.javaMud.Mud.handlers.Outlands.composeName(p.getPlaneId(), c.getX(),c.getY());
					MapItem mi;
					if(p.hasCity(c)) {
						HashSet<String> s = new HashSet<>();
						s.add("varos");
						mi = new MapItem(id,i,j,s,"");
						mi.addOverlay("varos");
					}
					else {
						mi = new MapItem(id,i,j,items[i+1][j+1],"");
					}
					mi.addOverlay(p.getOverlay(c));
					mi.setHeight(bitmap[i][j]);
					int pos = 0;
					for(String l:linekeys) {
						if(linestrings[i][j][pos]!=null) {
							mi.addLine(l, linestrings[i][j][pos]);
						}
						pos++;
					}
					ret.getItems().add(mi);
				}
			}
		}
		
		// kilogoljuk
		printLOG(bitmap);
		
		return ret;
	}
	/**
	 * kezeli a kozeppontbol sugar iranyba iranyulo vonalnyi latast
	 * @param x [0-Rec]
	 * @param y [0-Rec]
	 * @param c origo
	 * @param p
	 * @param bitmap
	 */
	private static void putHeight(int x, int y, Coordinate c, Plane p, int[][] bitmap) {
		Coordinate nc = fromBitmap(c, x, y);
		if(p.checkCoord(nc)) {
			bitmap[x][y] = p.getTerrainHeight(nc);
		}
		else {
			bitmap[x][y] = HEIGHT_OUT;
		}
		
		
		ArrayList<HeightData> line = drawLine(x,y,c,p);
		if(line.size()>0) {
			line.remove(0);
		}
		LOG.trace("----[ line ]---");
		double slope = Double.NEGATIVE_INFINITY;
		for(HeightData hd:line) {
			LOG.trace("x: "+ hd.getX() + " y: "+ hd.getY() + " h: "+ hd.getH() + " S: " + slope + " D: "+hd.getDeg());
			Coordinate l = toBitmap(c, hd.getX(), hd.getY());
			double d = Math.sqrt((l.getX()-R)*(l.getX()-R) + (l.getY()-R)*(l.getY()-R));
			if(d<=(R+0.5)) {
				//bitmap[l.getX()][l.getY()] = hd.getH();
				
				if(hd.getDeg() >= slope) {
					bitmap[l.getX()][l.getY()] = hd.getH();
					slope = hd.getDeg();
				}
				else {
					bitmap[l.getX()][l.getY()] = HEIGHT_CANTSEE;
				}
				
			}
			else {
				bitmap[l.getX()][l.getY()] = HEIGHT_OUT;
			}
			
		}
		printLOG(bitmap);
		
	}
	/**
	 * from wikipedia
	 * @param x [0-rec]
	 * @param y [0-rec]
	 * @param c
	 * @param p
	 * @param bitmap
	 * @return
	 */
	private static ArrayList<HeightData> drawLine(int x, int y, Coordinate c, Plane p){
		Coordinate c2 = fromBitmap(c, x, y);
		ArrayList<HeightData> ret = new ArrayList<>();
		
		int x1 = c.getX();
		int y1 = c.getY();
		int x2 = c2.getX();
		int y2 = c2.getY();
		int ch = p.getTerrainHeight(c);
		
		int dx = Math.abs(x2-x1);
	    int sx = (x1<x2)?1:-1;
	    int dy = -Math.abs(y2-y1);
	    int sy = (y1<y2)?1:-1;
	    int err = dx+dy;  /* error value e_xy */
	    while (true) {
	    	/* loop */
	    	
        	Coordinate nc = new Coordinate(x1, y1);
            if(p.checkCoord(nc)) {
            	int h = p.getTerrainHeight(nc);
            	HeightData hd = o.new HeightData(x1, y1, h, x1, x2, ch);
            	ret.add(hd);
            }
	        
	    	if ((x1==x2) && (y1==y2)) break;
	    	
	        int e2 = 2*err;
	        if (e2 >= dy) {
	        	/* e_xy+e_x > 0 */
	            err += dy;
	            x1 += sx;
	        }
	        if (e2 <= dx) {
	        	/* e_xy+e_y < 0 */
	            err += dx;
	            y1 += sy;
			}
		}
	
        return ret;
	}
	static final private void printLOG(int[][] bitmap) {
		if(!LOG.isTraceEnabled()) return;
		StringBuffer buff;
		for(int i=0;i<REC;i++) {
			buff = new StringBuffer();
			for(int j=0;j<REC;j++) {
				if(bitmap[j][i]>=0) {
					buff.append(String.format("%3d|", bitmap[j][i]));
				}
				else {
					buff.append("   |");
				}
			}
			LOG.trace(buff.toString());
		}
	}
	static private Coordinate toBitmap(Coordinate o,int x, int y) {
		return new Coordinate(x-o.getX()+R, y-o.getY()+R);
	}
	static private Coordinate fromBitmap(Coordinate o,int x, int y) {
		return fromBitmap(o,x, y,R);
	}
	static private Coordinate fromBitmap(Coordinate o,int x, int y, int r) {
		return new Coordinate(x-r+o.getX(),y-r+o.getY());
	}
}
