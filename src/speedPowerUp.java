import java.awt.Color;

/**
 * @author Maxime Dahan
 */
public class speedPowerUp{

	// Integers
	private int x, y, width, height;

	// Booleans
	private boolean eaten, eaten2;

	// Strings
	private Color color = new Color(117, 234, 0); // lime green

	public speedPowerUp(int startX, int startY, int startWidth, int startHeight, boolean eaten, boolean eaten2) {
		x = startX;
		y = startY;
		width = startWidth;
		height = startHeight;
		this.eaten = eaten;
		this.eaten2 = eaten2;
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
	public boolean gEaten() {
		return eaten;
	}
	public boolean gEaten2() {
		return eaten2;
	}
	public Color gColor() {
		return color;
	}
	
	// Setters
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
	public void sEaten(boolean e) {
		eaten = e;
	}
	public void sEaten2(boolean e) {
		eaten2 = e;
	}
}
