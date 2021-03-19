/**
 * @author Maxime Dahan
 */
public class Snowball implements Runnable{
	//Integers
	private int x, y, width, height, velX, velY, maxDistance = 35;
	
	//Booleans
	private boolean p1s, p2s, thrownP1, thrownP2, diagonal;

	public Snowball(int startX, int startY, int startWidth, int startHeight, boolean p1s, boolean p2s) {
		this.p1s = p1s;
		this.p2s = p2s;
		x = startX;
		y = startY;
		width = startWidth;
		height = startHeight;
	}

	public void run() {
		int travel = 0;
		while(true) {
			if(!p1s && !p2s && (velX != 0 || velY != 0)) {
				travel++;
				if(travel <= maxDistance && x <= 470 && y <= 470 && x >= 20 && y >= 20 && !diagonal) {
					x += velX;
					y += velY;
				}else if(travel <= (int)(maxDistance * 1.5) && x <= 470 && y <= 470 && x >= 20 && y >= 20 && diagonal) {
					x += velX;
					y += velY;
				}
				else {
					thrownP1 = false;
					thrownP2 = false;
					velX = 0;
					velY = 0;
					travel = 0;
				}
			}
			else {
				x += velX;
				y += velY;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Getters
	public int gX() {
		return x;
	}
	public int gY() {
		return y;
	}
	public int gW() {
		return width;
	}
	public int gH() {
		return height;
	}
	public int gVelX() {
		return velX;
	}
	public int gVelY() {
		return velY;
	}
	public boolean gP1s() {
		return p1s;
	}
	public boolean gP2s() {
		return p2s;
	}
	public int gMaxDistance() {
		return maxDistance;
	}
	public boolean gThrownP1() {
		return thrownP1;
	}
	public boolean gThrownP2() {
		return thrownP2;
	}
	
	// Setters	
	public void sDiagonal(boolean d) {
		diagonal = d;
	}
	public void sMaxDistance(int m) {
		maxDistance = m;
	}
	public void sVelX(int v) {
		velX = v;
	}
	public void sVelY(int v) {
		velY = v;
	}
	public void sX(int X) {
		x = X;
	}
	public void sY(int Y) {
		y = Y;
	}
	public void sW(int W) {
		width = W;
	}
	public void sH(int H) {
		height = H;
	}
	public void sP1s(boolean P1S) {
		p1s = P1S;
	}
	public void sP2s(boolean P2S) {
		p2s = P2S;
	}
	public void sThrownP1(boolean p) {
		thrownP1 = p;
	}
	public void sThrownP2(boolean p) {
		thrownP2 = p;
	}
}	
