/**
 * @author Ian Pourlotfali
 */
public class fort{
	//Integers
	private int x, y, width, height;
	
	public fort(int startX, int startY, int startWidth, int startHeight) {
		x = startX;
		y = startY;
		width = startWidth;
		height = startHeight;
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
}	
