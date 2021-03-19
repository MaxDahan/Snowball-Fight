import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Maxime Dahan
 */

public class Game implements Runnable {

	// constant integers
	private int speed = 2; // speed that players move at
	private int ballSpeed = 8; // the speed at which the ball moves at
	private int sSize = 10; // snowball size
	private int pSize = 25; // player size
	private int sPSize = 10; // speed up power up size
	private int frameHeight = 500;
	private int frameWidth = 500;
	private int speedUpSpawnRate = 10; // the rate at which speed power ups spawn
	private int speedUp = 2;

	// Walls/Edges
	private Wall top = new Wall(0, 0, 500, 10);
	private Wall bottom = new Wall(0, 490, 500, 10);
	private Wall left = new Wall(0, 0, 10, 500);
	private Wall right = new Wall(490, 0, 10, 500);
	
	// Fort
	private fort fort1 = new fort(130, 100, 250, 30); //on the top
								 //top wall                  //bottom wall				 //left wall				//right wall
	private fort[] fort1Walls = {new fort(130, 100, 270, 5), new fort(110, 130, 270, 5), new fort(130, 100, 5, 55), new fort(375, 85, 5, 50)}; //this is the list of walls and all of their stats
	
	private fort fort2 = new fort(130, 375, 250, 30); //on the bottom
								 //top wall                  //bottom wall				 //left wall				//right wall
	private fort[] fort2Walls = {new fort(130, 375, 270, 5), new fort(110, 405, 270, 5), new fort(130, 375, 5, 55), new fort(375, 360, 5, 50)}; //this is the list of walls and all of their stats
	

	// players
	private Player p1 = new Player(50, 50, pSize, pSize);
	private Player p2 = new Player(450, 450, pSize, pSize);

	// a snowballs, parameters are
	// starting x, y, width, height, if its p1's, and if its p2's
	private Snowball[] snowballs = { new Snowball(100, 100, sSize, sSize, false, false),
			new Snowball(400, 400, sSize, sSize, false, false),
			new Snowball(400, 100, sSize, sSize, false, false), new Snowball(100, 400, sSize, sSize, false, false) };

	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	private final ArrayList<Integer> MKP1 = new ArrayList<Integer>(); // multiple keys pressed for player 1
	private final ArrayList<Integer> MKP2 = new ArrayList<Integer>(); // multiple keys pressed for player 2
	private String direction = "", direction2 = "";//direction is the direction p1 is pointing and direction 2 is the same but for p2
	speedPowerUp SPU;
	private boolean doneSomething = false;
	
	
	//METHODS
	
	
	public Game() throws IOException {
		Random rand = new Random();
		int randX = rand.nextInt(frameWidth - 20);
		int randY = rand.nextInt(frameHeight - 20);
		while (snowballSpawnCheck(randX, randY, fort1) || snowballSpawnCheck(randX, randY, fort2)){
			randX = rand.nextInt(frameWidth - 20);
			randY = rand.nextInt(frameHeight - 20);
		}
		SPU = new speedPowerUp(randX, randY, sPSize,
				sPSize, false, false);

		Thread t2 = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(speedUpSpawnRate * 1000);
						
						int randX = rand.nextInt(frameWidth - 20);
						int randY = rand.nextInt(frameHeight - 20);
						while (snowballSpawnCheck(randX, randY, fort1) || snowballSpawnCheck(randX, randY, fort2)){
							randX = rand.nextInt(frameWidth - 20);
							randY = rand.nextInt(frameHeight - 20);
						}
						SPU = new speedPowerUp(randX, randY, sPSize,
								sPSize, false, false);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t2.start();

		for (int i = 0; i < snowballs.length; i++) {
			new Thread(snowballs[i]).start();
		}
		new Thread(p1).start();
		new Thread(p2).start();

		frame = new JFrame("Snowball Fight");
		frame.setSize(500, 500);
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(frameWidth, frameHeight));
		panel.setLayout(null);
		canvas = new Canvas();
		canvas.setBounds(0, 0, frameWidth, frameHeight);
		canvas.setIgnoreRepaint(true);
		panel.add(canvas);
		canvas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				moveItPress(evt);
			}

			public void keyReleased(KeyEvent evt) {
				moveItRelease(evt);
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		canvas.requestFocus();
		
		frame.setVisible(true);
	}
	
	//Painting
	public void Paint() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, frameWidth, frameHeight); // clears the canvas from 0, 0 to the width and height
		Paint(g);
		bufferStrategy.show();
	}
	protected void Paint(Graphics2D g) {
		g.setColor(Color.BLACK);

		// Walls
		// Top wall
		g.fillRect(top.gX(), top.gY(), top.gW(), top.gH());
		// Bottom wall
		g.fillRect(bottom.gX(), bottom.gY(), bottom.gW(), bottom.gH());
		// left wall
		g.fillRect(left.gX(), left.gY(), left.gW(), left.gH());
		// Right wall
		g.fillRect(right.gX(), right.gY(), right.gW(), right.gH());
		
		g.setColor(Color.CYAN);
		//Forts
		//fort1
		g.fillRect(fort1.gX(), fort1.gY(), fort1.gW(), fort1.gH());
		//fort2
		g.fillRect(fort2.gX(), fort2.gY(), fort2.gW(), fort2.gH());
		
		g.setColor(Color.BLACK);
		
		//This for loop is used to check the boundaries of the forts and check if they are in the right places
		//for(int i = 0; i < 4; i++) {
			//g.fillRect(fort1Walls[i].gX(), fort1Walls[i].gY(), fort1Walls[i].gW(), fort1Walls[i].gH());
		//}
		//for(int i = 0; i < 4; i++) {
			//g.fillRect(fort2Walls[i].gX(), fort2Walls[i].gY(), fort2Walls[i].gW(), fort2Walls[i].gH());
		//}
		// Snowballs
		for (
			int i = 0; i < snowballs.length; i++) {
			g.fillRect(snowballs[i].gX(), snowballs[i].gY(), snowballs[i].gW(), snowballs[i].gH());
		}
		
		if (containsSP(p1, SPU)) {
			SPU.sEaten(true);
			SPU.sX(-1000);
			SPU.sY(-1000);
			Thread t2 = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(3000);
						SPU.sEaten(false);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			});
			t2.start();
		}
		if (containsSP(p2, SPU)) {
			SPU.sEaten2(true);
			SPU.sX(-1000);
			SPU.sY(-1000);
			Thread t2 = new Thread(new Runnable() {

				public void run() {
					try {
						Thread.sleep(3000);
						SPU.sEaten(false);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			});
			t2.start();
		}

		// Speed Power Up
		g.setColor(SPU.gColor());
		g.fillRect(SPU.gX(), SPU.gY(), SPU.gW(), SPU.gH());
		// Players
		
		//try {
			//Image image = ImageIO.read(new File(System.getProperty("user.dir") + "/data/SnowballPlayerBLUE.png"));
			//g.drawImage(image, p1.gX(), p1.gY(), image.getWidth(null), image.getHeight(null), null);
		//} catch (IOException e) {System.err.println("Error fetching image");}
		// player 1
		g.setColor(Color.BLUE);
		g.fillRect(p1.gX(), p1.gY(), p1.gW(), p1.gH());

		//try {
		//Image image = ImageIO.read(new File(System.getProperty("user.dir") + "/data/SnowballPlayerRED.png"));
		//g.drawImage(image, p1.gX(), p1.gY(), image.getWidth(null), image.getHeight(null), null);
		//} catch (IOException e) {System.err.println("Error fetching image");}
		// player 2
		g.setColor(Color.RED);
		g.fillRect(p2.gX(), p2.gY(), p2.gW(), p2.gH());
	}

	
	//KeyListeners
	public void moveItPress(KeyEvent evt) {
		int key = evt.getKeyCode();

		// Player 1/Blue
		if (key == KeyEvent.VK_D) {
			MKP1.add(1);
			direction = "down";
			if (SPU.gEaten()) {
				p1.sVelY(speed + speedUp);
			} else {
				p1.sVelY(speed);
			}
		} else if (key == KeyEvent.VK_E) {
			direction = "up";
			MKP1.add(2);
			if (SPU.gEaten()) {
				p1.sVelY(-1 * speed - speedUp);
			} else {
				p1.sVelY(-1 * speed);
			}
		} else if (key == KeyEvent.VK_S) {
			direction = "left";
			MKP1.add(3);
			if (SPU.gEaten()) {
				p1.sVelX(-1 * speed - speedUp);
			} else {
				p1.sVelX(speed * -1);
			}
		} else if (key == KeyEvent.VK_F) {
			direction = "right";
			MKP1.add(4);
			if (SPU.gEaten()) {
				p1.sVelX(speed + speedUp);
			} else {
				p1.sVelX(speed);
			}
		} else if (key == KeyEvent.VK_Q) {
			// checking p1
			doneSomething = false; //this is used so you dont pick up a snowball and throw one at the same time
			for (int i = 0; i < snowballs.length; i++) {
				if (snowballs[i].gP1s() && !doneSomething) {
					snowballs[i].sThrownP1(true);
					if (p1.gVelX() > 0 && p1.gVelY() < 0) { // Up right
						snowballs[i].sP1s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5));
						snowballs[i].sVelY((int) (ballSpeed / 1.5) * -1);
					} else if (p1.gVelX() > 0 && p1.gVelY() > 0) { // Down right
						snowballs[i].sP1s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5));
						snowballs[i].sVelY((int) (ballSpeed / 1.5));
					} else if (p1.gVelX() < 0 && p1.gVelY() < 0) { // Up left
						snowballs[i].sP1s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5) * -1);
						snowballs[i].sVelY((int) (ballSpeed / 1.5) * -1);
					} else if (p1.gVelX() < 0 && p1.gVelY() > 0) { // Down left
						snowballs[i].sP1s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5) * -1);
						snowballs[i].sVelY((int) (ballSpeed / 1.5));
					} else if (direction.equals("down")) {
						snowballs[i].sP1s(false);
						snowballs[i].sVelX(0);
						snowballs[i].sVelY(ballSpeed);
					} else if (direction.equals("up")) {
						snowballs[i].sP1s(false);
						snowballs[i].sVelX(0);
						snowballs[i].sVelY(ballSpeed * -1);
					} else if (direction.equals("left")) {
						snowballs[i].sP1s(false);
						snowballs[i].sVelX(ballSpeed * -1);
						snowballs[i].sVelY(0);
					} else if (direction.equals("right")) {
						snowballs[i].sP1s(false);
						snowballs[i].sVelX(ballSpeed);
						snowballs[i].sVelY(0);
					}
					snowballs[i].sW(10);
					snowballs[i].sH(10);
				} else if (contains(p1, snowballs[i]) && !doneSomething && !snowballs[i].gP2s()) {
					doneSomething = true;
					// makes the snowball bigger which makes it look like it goes up
					snowballs[i].sW(15);
					snowballs[i].sH(15);
					snowballs[i].sP1s(true);
					/*if (direction.equals("down")) {
						setSnowballY(true, false, true, true);
					} else if (direction.equals("up")) {
						setSnowballY(true, true, false, true);
					} else if (direction.equals("left")) {
						setSnowballX(true, false, true, true);
					} else if (direction.equals("right")) {
						setSnowballX(true, true, false, true);
					}*/
					snowballs[i].sVelX(p1.gVelX());
					snowballs[i].sVelY(p1.gVelY());
				}
			}
		}

		// Player 2/Red
		if (key == KeyEvent.VK_DOWN) {
			direction2 = "down";
			MKP2.add(1);
			if (SPU.gEaten2()) {
				p2.sVelY(speed + speedUp);
			} else {
				p2.sVelY(speed);
			}
		} else if (key == KeyEvent.VK_UP) {
			direction2 = "up";
			MKP2.add(2);
			if (SPU.gEaten2()) {
				p2.sVelY(-1 * speed - speedUp);
			} else {
				p2.sVelY(-1 * speed);
			}
		} else if (key == KeyEvent.VK_LEFT) {
			direction2 = "left";
			MKP2.add(3);
			if (SPU.gEaten2()) {
				p2.sVelX(-1 * speed - speedUp);
			} else {
				p2.sVelX(speed * -1);
			}
		} else if (key == KeyEvent.VK_RIGHT) {
			direction2 = "right";
			MKP2.add(4);
			if (SPU.gEaten2()) {
				p2.sVelX(speed + speedUp);
			} else {
				p2.sVelX(speed);
			}
		} else if (key == KeyEvent.VK_M) {
			// checking p2
			doneSomething = false; //this is used so you dont pick up a snowball and throw one at the same time
			for (int i = 0; i < snowballs.length; i++) {
				if (snowballs[i].gP2s() && !doneSomething) {
					doneSomething = true;
					snowballs[i].sThrownP2(true);
					if (p2.gVelX() > 0 && p2.gVelY() < 0) {// Up right
						snowballs[i].sP2s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5));
						snowballs[i].sVelY((int) (ballSpeed / 1.5) * -1);
					} else if (p2.gVelX() > 0 && p2.gVelY() > 0) { // Down right
						snowballs[i].sP2s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5));
						snowballs[i].sVelY((int) (ballSpeed / 1.5));
					} else if (p2.gVelX() < 0 && p2.gVelY() < 0) { // Up left
						snowballs[i].sP2s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5) * -1);
						snowballs[i].sVelY((int) (ballSpeed / 1.5) * -1);
					} else if (p2.gVelX() < 0 && p2.gVelY() > 0) { // Down left
						snowballs[i].sP2s(false);
						snowballs[i].sDiagonal(true);
						snowballs[i].sVelX((int) (ballSpeed / 1.5) * -1);
						snowballs[i].sVelY((int) (ballSpeed / 1.5));
					} else if (direction2.equals("down")) {
						snowballs[i].sP2s(false);
						snowballs[i].sVelX(0);
						snowballs[i].sVelY(ballSpeed);
					} else if (direction2.equals("up")) {
						snowballs[i].sP2s(false);
						snowballs[i].sVelX(0);
						snowballs[i].sVelY(ballSpeed * -1);
					} else if (direction2.equals("left")) {
						snowballs[i].sP2s(false);
						snowballs[i].sVelX(ballSpeed * -1);
						snowballs[i].sVelY(0);
					} else if (direction2.equals("right")) {
						snowballs[i].sP2s(false);
						snowballs[i].sVelX(ballSpeed);
						snowballs[i].sVelY(0);
					}
					snowballs[i].sW(10);
					snowballs[i].sH(10);
				} else if (contains(p2, snowballs[i]) && !doneSomething &&!snowballs[i].gP1s()) {
					doneSomething = true;
					// makes the snowball bigger which makes it look like it goes up
					snowballs[i].sW(15);
					snowballs[i].sH(15);
					snowballs[i].sP2s(true);
					/*if (direction2.equals("down")) {
						setSnowballY(false, false, true, true);
					} else if (direction2.equals("up")) {
						setSnowballY(false, true, false, true);
					} else if (direction2.equals("left")) {
						setSnowballX(false, false, true, true);
					} else if (direction2.equals("right")) {
						setSnowballX(false, true, false, true);
					}*/
					snowballs[i].sVelX(p2.gVelX());
					snowballs[i].sVelY(p2.gVelY());
				}
			}
		}
	}
	public void moveItRelease(KeyEvent evt) {
		int key = evt.getKeyCode();

		// Player 1/Blue
		if (key == KeyEvent.VK_D) {
			if (MKP1.size() < 1) {
				p1.sVelY(0);
				p1.sVelX(0);
				resetSnowballsVelocity();
			} else {
				if (MKP1.get(MKP1.size() - 1) != 2) {
					p1.sVelY(0);
					ArrayList<Integer> inUse = snowballsInUse1();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelY(0);
					}
				}
			}
		} else if (key == KeyEvent.VK_E) {
			if (MKP1.size() < 1) {
				p1.sVelY(0);
				p1.sVelX(0);
				resetSnowballsVelocity();
			} else {
				if (MKP1.get(MKP1.size() - 1) != 1) {
					p1.sVelY(0);
					ArrayList<Integer> inUse = snowballsInUse1();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelY(0);
					}
				}
			}
		} else if (key == KeyEvent.VK_S) {
			if (MKP1.size() < 1) {
				p1.sVelX(0);
				p1.sVelY(0);
				resetSnowballsVelocity();
			} else {
				if (MKP1.get(MKP1.size() - 1) != 4) {
					p1.sVelX(0);
					ArrayList<Integer> inUse = snowballsInUse1();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelX(0);
					}
				}
			}
		} else if (key == KeyEvent.VK_F) {
			if (MKP1.size() < 1) {
				p1.sVelX(0);
				p1.sVelY(0);
				resetSnowballsVelocity();
			} else {
				if (MKP1.get(MKP1.size() - 1) != 3) {
					p1.sVelX(0);
					ArrayList<Integer> inUse = snowballsInUse1();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelX(0);
					}
				}
			}
		}

		// Player 2/Red
		if (key == KeyEvent.VK_DOWN) {
			if (MKP2.size() < 1) {
				p2.sVelY(0);
				p2.sVelX(0);
				resetSnowballsVelocity();
			} else {
				if (MKP2.get(MKP2.size() - 1) != 2) {
					p2.sVelY(0);
					ArrayList<Integer> inUse = snowballsInUse2();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelY(0);
					}
				}

			}

		} else if (key == KeyEvent.VK_UP) {
			if (MKP2.size() < 1) {
				p2.sVelY(0);
				p2.sVelX(0);
				resetSnowballsVelocity();
			} else {
				if (MKP2.get(MKP2.size() - 1) != 1) {
					p2.sVelY(0);
					ArrayList<Integer> inUse = snowballsInUse2();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelY(0);
					}
				}

			}
		} else if (key == KeyEvent.VK_LEFT) {
			if (MKP2.size() < 1) {
				p2.sVelX(0);
				p2.sVelY(0);
				resetSnowballsVelocity();
			} else {
				if (MKP2.get(MKP2.size() - 1) != 4) {
					p2.sVelX(0);
					ArrayList<Integer> inUse = snowballsInUse2();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelX(0);
					}
				}
			}
		} else if (key == KeyEvent.VK_RIGHT) {

			if (MKP2.size() < 1) {
				p2.sVelX(0);
				p2.sVelY(0);
				resetSnowballsVelocity();
			} else {
				if (MKP2.get(MKP2.size() - 1) != 3) {
					p2.sVelX(0);
					ArrayList<Integer> inUse = snowballsInUse2();
					for (int i = 0; i < inUse.size(); i++) {
						snowballs[inUse.get(i)].sVelX(0);
					}
				}
			}
		}
	}

	public void run() {
		while (true) {
			checkWall();
			checkFort();
			checkSnowball();
			for (int i = 0; i < snowballs.length; i++) {
				if (!snowballs[i].gP1s() && !snowballs[i].gP2s()
						&& (snowballs[i].gVelX() != 0 || snowballs[i].gVelY() != 0)) {
					if (snowballs[i].gThrownP1() && containsHit(p2, snowballs[i])) {
						frame.dispose();
						endScreen end = new endScreen(true);
					} else if (snowballs[i].gThrownP2() && containsHit(p1, snowballs[i])) {
						frame.dispose();
						endScreen end = new endScreen(false);
					}
				}
			}
			setSnowballPositions();
			Paint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
	}

	//Contains
	public boolean contains(Player p, Snowball s) {
		boolean inX = p.gX() - 10 <= s.gX() && p.gX() + 10 + p.gW() >= s.gX();
		boolean inY = p.gY() - 10 <= s.gY() && p.gY() + 10 + p.gH() >= s.gY();
		return inX && inY;
	}
	public boolean containsHit(Player p, Snowball s) {
		boolean inX = p.gX() <= s.gX() && p.gX() + p.gW() >= s.gX();
		boolean inY = p.gY() <= s.gY() && p.gY() + p.gH() >= s.gY();
		return inX && inY;
	}
	public boolean containsWall(Player p, Wall s) {
		boolean inX = s.gX() <= p.gX() && s.gX() + s.gW() >= p.gX();
		boolean inY = s.gY() <= p.gY() && s.gY() + s.gH() >= p.gY();
		return inX && inY;
	}
	public boolean containsSP(Player p, speedPowerUp s) {
		boolean inX = p.gX() <= s.gX() && p.gX() + p.gW() >= s.gX();
		boolean inY = p.gY() <= s.gY() && p.gY() + p.gH() >= s.gY();
		return inX && inY;
	}
	public boolean containsWall2(Player p, Wall s) { //another changed copy of contains wall was made so the player did not have to
		// be in the bottom and right wall for it to push them,
		// cause the contains wall method worked well for top and left but not bottom
		// and right
		boolean inX = s.gX() - 25 <= p.gX() && s.gX() - 25 + s.gW() >= p.gX();
		boolean inY = s.gY() - 25 <= p.gY() && s.gY() - 25 + s.gH() >= p.gY();
		return inX && inY;
	}
	public boolean fortContainsSnowball(fort p, Snowball s) {
		boolean inX = p.gX() <= s.gX() && p.gX() + p.gW() >= s.gX();
		boolean inY = p.gY() <= s.gY() && p.gY() + p.gH() >= s.gY();
		return inX && inY;
	}
	public boolean containsFort(Player p, fort s) {
		boolean inX = s.gX() <= p.gX() && s.gX() + s.gW() >= p.gX();
		boolean inY = s.gY() - 5 <= p.gY() && s.gY() + s.gH() - 5>= p.gY();
		return inX && inY;
	}
	public boolean containsFort2(Player p, fort s) {
		boolean inX = s.gX() - 25 <= p.gX() && s.gX() - 25 + s.gW() >= p.gX();
		boolean inY = s.gY() - 25 <= p.gY() && s.gY() - 25 + s.gH() >= p.gY();
		return inX && inY;
	}
	
	
	//Checkers
	public void checkWall() {//checks to see if person in wall borders
		//Player one not eaten speed up
		if(!SPU.gEaten()) {
			if (containsWall(p1, top)) {p1.sY(p1.gY() + 3);}
			if (containsWall2(p1, bottom)) {p1.sY(p1.gY() - 3);}
			if (containsWall(p1, left)) {p1.sX(p1.gX() + 3);}
			if (containsWall2(p1, right)) {p1.sX(p1.gX() - 3);}
		}else if(SPU.gEaten()) { //player one has eaten speed up
			if (containsWall(p1, top)) {p1.sY(p1.gY() + (5 + speedUp));}
			if (containsWall2(p1, bottom)) {p1.sY(p1.gY() - (5 + speedUp));}
			if (containsWall(p1, left)) {p1.sX(p1.gX() + (5 + speedUp));}
			if (containsWall2(p1, right)) {p1.sX(p1.gX() - (5 + speedUp));}
		}
		
		//player two has not eaten speed up
		if(!SPU.gEaten2()) {
			if (containsWall(p2, top)) {p2.sY(p2.gY() + 3);}
			if (containsWall2(p2, bottom)) {p2.sY(p2.gY() - 3);}
			if (containsWall(p2, left)) {p2.sX(p2.gX() + 3);}
			if (containsWall2(p2, right)) {p2.sX(p2.gX() - 3);}
		}else if(SPU.gEaten2()) {//player two has eaten speed up
			if (containsWall(p2, top)) {p2.sY(p2.gY() + (5 + speedUp));}
			if (containsWall2(p2, bottom)) {p2.sY(p2.gY() - (5 + speedUp));}
			if (containsWall(p2, left)) {p2.sX(p2.gX() + (5 + speedUp));}
			if (containsWall2(p2, right)) {p2.sX(p2.gX() - (5 + speedUp));}
		}
	}
	public void checkFort() {//Checks to make sure players stay out of the forts by adding "boundaries"
		if(!SPU.gEaten()) {
			//p1 First fort 
			if(containsFort2(p1, fort1Walls[0])) {p1.sY(p1.gY() - 5);}//checking the top wall
			else if(containsFort(p1, fort1Walls[1])) {p1.sY(p1.gY() + 5);}//checking the bottom wall
			else if(containsFort2(p1, fort1Walls[2])) {p1.sX(p1.gX() - 5);}//checking the left wall
			else if(containsFort(p1, fort1Walls[3])) {p1.sX(p1.gX() + 5);}//checking the right wall
		
			//p1 Second fort 
			if(containsFort2(p1, fort2Walls[0])) {p1.sY(p1.gY() - 5);}//checking the top wall
			else if(containsFort(p1, fort2Walls[1])) {p1.sY(p1.gY() + 5);}//checking the bottom wall
			else if(containsFort2(p1, fort2Walls[2])) {p1.sX(p1.gX() - 5);}//checking the left wall
			else if(containsFort(p1, fort2Walls[3])) {p1.sX(p1.gX() + 5);}//checking the right wall
		}else if(SPU.gEaten()) {
			//p1 First fort
			if(containsFort2(p1, fort1Walls[0])) {p1.sY(p1.gY() - (10 + speedUp));}//checking the top wall
			else if(containsFort(p1, fort1Walls[1])) {p1.sY(p1.gY() + (10 + speedUp));}//checking the bottom wall
			else if(containsFort2(p1, fort1Walls[2])) {p1.sX(p1.gX() - (10 + speedUp));}//checking the left wall
			else if(containsFort(p1, fort1Walls[3])) {p1.sX(p1.gX() + (10 + speedUp));}//checking the right wall
			
			//p1 Second fort
			if(containsFort2(p1, fort2Walls[0])) {p1.sY(p1.gY() - (10 + speedUp));}//checking the top wall
			else if(containsFort(p1, fort2Walls[1])) {p1.sY(p1.gY() + (10 + speedUp));}//checking the bottom wall
			else if(containsFort2(p1, fort2Walls[2])) {p1.sX(p1.gX() - (10 + speedUp));}//checking the left wall
			else if(containsFort(p1, fort2Walls[3])) {p1.sX(p1.gX() + (10 + speedUp));}//checking the right wall
		}
		
		if(!SPU.gEaten2()) {
			//p2 First fort
			if(containsFort2(p2, fort1Walls[0])) {p2.sY(p2.gY() - 5);}//checking the top wall
			else if(containsFort(p2, fort1Walls[1])) {p2.sY(p2.gY() + 5 );}//checking the bottom wall
			else if(containsFort2(p2, fort1Walls[2])) {p2.sX(p2.gX() - 5 );}//checking the left wall
			else if(containsFort(p2, fort1Walls[3])) {p2.sX(p2.gX() + 5);}//checking the right wall
			
			//p2 Second fort
			if(containsFort2(p2, fort2Walls[0])) {p2.sY(p2.gY() - 5);}//checking the top wall
			if(containsFort(p2, fort2Walls[1])) {p2.sY(p2.gY() + 5);}//checking the bottom wall
			if(containsFort2(p2, fort2Walls[2])) {p2.sX(p2.gX() - 5);}//checking the left wall
			if(containsFort(p2, fort2Walls[3])) {p2.sX(p2.gX() + 5) ;}//checking the right wall
		}else if(SPU.gEaten2()) {
			//p2 First fort
			if(containsFort2(p2, fort1Walls[0])) {p2.sY(p2.gY() - (10 + speedUp));}//checking the top wall
			else if(containsFort(p2, fort1Walls[1])) {p2.sY(p2.gY() + (10 + speedUp));}//checking the bottom wall
			else if(containsFort2(p2, fort1Walls[2])) {p2.sX(p2.gX() - (10 + speedUp));}//checking the left wall
			else if(containsFort(p2, fort1Walls[3])) {p2.sX(p2.gX() + (10 + speedUp));}//checking the right wall
			
			//p2 Second fort
			if(containsFort2(p2, fort2Walls[0])) {p2.sY(p2.gY() - (10 + speedUp));}//checking the top wall
			if(containsFort(p2, fort2Walls[1])) {p2.sY(p2.gY() + (10 + speedUp));}//checking the bottom wall
			if(containsFort2(p2, fort2Walls[2])) {p2.sX(p2.gX() - (10 + speedUp));}//checking the left wall
			if(containsFort(p2, fort2Walls[3])) {p2.sX(p2.gX() + (10 + speedUp));}//checking the right wall
		}
	}
	public void checkSnowball() {//checks to see if snow ball is in wall borders
		for(int i = 0; i < snowballs.length; i++) {
			if(fortContainsSnowball(fort1, snowballs[i])) {
				snowballs[i].sVelX(0); 
				snowballs[i].sVelY(0);
				snowballs[i].sThrownP1(false);
				snowballs[i].sThrownP2(false);
			}
			if(fortContainsSnowball(fort2, snowballs[i])) {
				snowballs[i].sVelX(0); 
				snowballs[i].sVelY(0);
				snowballs[i].sThrownP1(false);
				snowballs[i].sThrownP2(false);
			}
		}
	}
	public ArrayList<Integer> snowballsInUse1() {//checks which snow balls are in use by p1
		ArrayList<Integer> use = new ArrayList<Integer>();
		for (int i = 0; i < snowballs.length; i++) {
			if (snowballs[i].gP1s()) {
				use.add(i);
			}
		}
		return use;
	}
	public ArrayList<Integer> snowballsInUse2() {//checks which snow balls are in use by p2
		ArrayList<Integer> use = new ArrayList<Integer>();
		for (int i = 0; i < snowballs.length; i++) {
			if (snowballs[i].gP2s()) {
				use.add(i);
			}
		}
		return use;
	}
	
	
	//Getters
	public int gFrameWidth() {
		return frameWidth;
	}
	public int gFrameHeigth() {
		return frameHeight;
	}

	
	//Setters
	public void setSnowballPositions() {//Sets the position of the snow ball compared to where the player and where he is pointing
		for (int i = 0; i < snowballs.length; i++) {
			if (snowballs[i].gP1s()) {
				if (p1.gVelX() > 0 && p1.gVelY() < 0) { // Up right
					snowballs[i].sX(p1.gX() + (p1.gW() / 2 + p1.gW() / 4));
					snowballs[i].sY(p1.gY() - (p1.gH() / 2 - p1.gH() / 4));
				} else if (p1.gVelX() > 0 && p1.gVelY() > 0) { // Down right
					snowballs[i].sX(p1.gX() + (p1.gW() / 2 + p1.gW() / 4));
					snowballs[i].sY(p1.gY() + (p1.gH() / 2 + p1.gH() / 4));
				} else if (p1.gVelX() < 0 && p1.gVelY() < 0) { // Up left
					snowballs[i].sX(p1.gX() - (p1.gW() / 2 - p1.gW() / 4));
					snowballs[i].sY(p1.gY() - (p1.gH() / 2 - p1.gH() / 4));
				} else if (p1.gVelX() < 0 && p1.gVelY() > 0) { // Down left
					snowballs[i].sX(p1.gX() - (p1.gW() / 2 - p1.gW() / 4));
					snowballs[i].sY(p1.gY() + (p1.gH() / 2 + p1.gH() / 4));
				} else if (direction.equals("down")) {
					setSnowballY(true, false, true, false);
				} else if (direction.equals("up")) {
					setSnowballY(true, true, false, false);
				} else if (direction.equals("left")) {
					setSnowballX(true, false, true, false);
				} else if (direction.equals("right")) {
					setSnowballX(true, true, false, false);
				}
			}
		}
		for (int i = 0; i < snowballs.length; i++) {
			if (snowballs[i].gP2s()) {
				if (p2.gVelX() > 0 && p2.gVelY() < 0) { // Up right
					snowballs[i].sX(p2.gX() + (p2.gW() / 2 + p2.gW() / 4));
					snowballs[i].sY(p2.gY() - (p2.gH() / 2 - p2.gH() / 4));
				} else if (p2.gVelX() > 0 && p2.gVelY() > 0) { // Down right
					snowballs[i].sX(p2.gX() + (p2.gW() / 2 + p2.gW() / 4));
					snowballs[i].sY(p2.gY() + (p2.gH() / 2 + p2.gH() / 4));
				} else if (p2.gVelX() < 0 && p2.gVelY() < 0) { // Up left
					snowballs[i].sX(p2.gX() - (p2.gW() / 2 - p2.gW() / 4));
					snowballs[i].sY(p2.gY() - (p2.gH() / 2 - p2.gH() / 4));
				} else if (p2.gVelX() < 0 && p2.gVelY() > 0) { // Down left
					snowballs[i].sX(p2.gX() - (p2.gW() / 2 - p2.gW() / 4));
					snowballs[i].sY(p2.gY() + (p2.gH() / 2 + p2.gH() / 4));
				} else if (direction2.equals("down")) {
					setSnowballY(false, false, true, false);
				} else if (direction2.equals("up")) {
					setSnowballY(false, true, false, false);
				} else if (direction2.equals("left")) {
					setSnowballX(false, false, true, false);
				} else if (direction2.equals("right")) {
					setSnowballX(false, true, false, false);
				}
			}
		}
	}
	public void resetSnowballsVelocity() {//resets the velocities of all of the snow balls
		for (int i = 0; i < snowballs.length; i++) {
			if (snowballs[i].gP1s() || snowballs[i].gP2s()) {
				snowballs[i].sVelX(0);
				snowballs[i].sVelY(0);
			}
		}
	}
	public boolean snowballSpawnCheck(int x, int y, fort s) {
		boolean inX = s.gX() <= x && s.gX() + s.gW() >= x;
		boolean inY = s.gY() - 5 <= y && s.gY() + s.gH() - 5 >= y;
		return inX && inY;
	}
	public void setSnowballY(boolean p, boolean up, boolean down, boolean original) {//Sets the X position of the snow ball
		if (p) {
			for (int i = 0; i < snowballs.length; i++) {
				if (snowballs[i].gP1s() && down) {
					snowballs[i].sY(p1.gY() + 20);
					snowballs[i].sX(p1.gX() + 5);
					if (!original) {
						if (SPU.gEaten()) {
							snowballs[i].sVelY(speed + speedUp);
						} else {
							snowballs[i].sVelY(speed);
						}
					}
				} else if (snowballs[i].gP1s() && up) {
					snowballs[i].sY(p1.gY() - 10);
					snowballs[i].sX(p1.gX() + 5);
					if (!original) {
						if (SPU.gEaten()) {
							snowballs[i].sVelY(-1 * speed - speedUp);
						} else {
							snowballs[i].sVelY(speed * -1);
						}
					}
				}
			}	
		} else {
			for (int i = 0; i < snowballs.length; i++) {
				if (snowballs[i].gP2s() && down) {
					snowballs[i].sY(p2.gY() + 20);
					snowballs[i].sX(p2.gX() + 5);
					if (!original) {
						if (SPU.gEaten2()) {
							snowballs[i].sVelY(speed + speedUp);
						} else {
							snowballs[i].sVelY(speed);
						}
					}
				} else if (snowballs[i].gP2s() && up) {
					snowballs[i].sY(p2.gY() - 10);
					snowballs[i].sX(p2.gX() + 5);
					if (!original) {
						if (SPU.gEaten2()) {
							snowballs[i].sVelY(-1 * speed - speedUp);
						} else {
							snowballs[i].sVelY(speed * -1);
						}
					}
				}
			}
		}
	}
	public void setSnowballX(boolean p, boolean right, boolean left, boolean original) {//Sets the Y position of the snow ball
		if (p) {
			for (int i = 0; i < snowballs.length; i++) {
				if (snowballs[i].gP1s() && left) {
					snowballs[i].sX(p1.gX() - 10);
					snowballs[i].sY(p1.gY() + 5);
					if (!original) {
						if (SPU.gEaten()) {
							snowballs[i].sVelX(-1 * speed - speedUp);
						} else {
							snowballs[i].sVelX(speed * -1);
						}
					}
				} else if (snowballs[i].gP1s() && right) {
					snowballs[i].sX(p1.gX() + 20);
					snowballs[i].sY(p1.gY() + 5);
					if (!original) {
						if (SPU.gEaten()) {
							snowballs[i].sVelX(speed + speedUp);
						} else {
							snowballs[i].sVelX(speed);
						}
					}
				}
			}
		} else {
			for (int i = 0; i < snowballs.length; i++) {
				if (snowballs[i].gP2s() && left) {
					snowballs[i].sX(p2.gX() - 10);
					snowballs[i].sY(p2.gY() + 5);
					if (!original) {
						if (SPU.gEaten2()) {
							snowballs[i].sVelX(-1 * speed - speedUp);
						} else {
							snowballs[i].sVelX(speed * -1);
						}
					}
				} else if (snowballs[i].gP2s() && right) {
					snowballs[i].sX(p2.gX() + 20);
					snowballs[i].sY(p2.gY() + 5);
					if (!original) {
						if (SPU.gEaten2()) {
							snowballs[i].sVelX(speed + speedUp);
						} else {
							snowballs[i].sVelX(speed);
						}
					}
				}
			}
		}
	}
	
	// the main method can be used for easier testing
	public static void main(String[] args) throws IOException {
		Game ex = new Game();
		new Thread(ex).start();
	}
}