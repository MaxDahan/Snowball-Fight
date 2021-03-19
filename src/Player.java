import java.awt.Graphics2D;

/**
 * @author Maxime Dahan
 */
public class Player implements Runnable{
	
	//Integers
	private int x, y, width, height, velX, velY;
	
	public Player(int startX, int startY, int startWidth, int startHeight) {
		x = startX;
		y = startY;
		width = startWidth;
		height = startHeight;
	}
	
	//Getters
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
	
	//Setters
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

	public void run() {
		while(true) {
			x += velX;
			y += velY;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
