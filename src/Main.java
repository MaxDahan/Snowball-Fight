import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Maxime Dahan
 */
public class Main implements Runnable {
	
	
	// Integers
	private int	FRAMEWIDTH = 518;
	private int	FRAMEHEIGHT = 348;
	private int mainRowStart = 120; //starting main row in control info screen
	private int subRowStart = 275; //second row x start in control info screen
	
	// Fonts
	private Font mainFont = new Font("Arial", Font.BOLD, 25);
	private Font subFont = new Font("Arial", Font.BOLD, 16);
	private Font arrowFont = new Font("Arial", Font.BOLD, 20);
	
	// JFrames
	private JFrame startF = new JFrame("Snowball Fight"); // start frame
	private JFrame controlF = new JFrame("Snowball Fight"); // control frame
	public JFrame frame = new JFrame("Controls"); //control info frame

	// JPanels
	private JLayeredPane startP = new JLayeredPane(); // start panel
	private JLayeredPane controlP = new JLayeredPane(); // control panel

	// JButtons
	private JLabel startB = new JLabel("Start"); // play button at the start screen
	private JButton controlB1 = new JButton("P1 - Press Q To Start"); // p1 button at the control screen
	private JButton controlB2 = new JButton("P2 - Press M To Start"); // p2 button at the control screen
	private JButton controlB3 = new JButton(); //Back arrow
	private JButton controlB4 = new JButton("Controls"); // shows controls

	// JLabels
	private JLabel controlBack = new JLabel();
	//private static JLabel controlL = new JLabel("Back");
	//private static JLabel controlL1 = new JLabel("P1 --  UP = E  --  Down = D  --  Left = S  --  Right = F  --  Throw & Pfmsick-Up = Q "); // p1 controls on control																				// screen
	//private static JLabel controlL2 = new JLabel("P2 --  MOVE = Arrow keys  --  Throw & Pick-Up = M"); // p2 controls on conftrol screen

	// Booleans
	private boolean P1Ready = false; // when p1 is ready to go in control screen
	private boolean P2Ready = false; // when p2 is ready to go in control screen
	
	// Buffer Strategy
	private BufferStrategy bufferStrategy;
	
	// Colors
	private Color P1Color = new Color(0, 0, 204); //Blue
	private Color P2Color = new Color(255, 0, 0); //Red
	
	
	//METHODS
	
	// Button Color Changing When User Hovers Over Button
	public boolean mouseContainsButton(JLabel p, double x, double y) {
		boolean inX = p.getX() - 15 <= x && p.getX() + 15 + p.getWidth() >= x;
		boolean inY = p.getY() + 20 <= y && p.getY() + 20 + p.getHeight() >= y;
		return inX && inY;
	}
	
	public void run() {
		while(true) {
			if(mouseContainsButton(startB, MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY())) {
				startB.setForeground(Color.CYAN);
			}else {
				startB.setForeground(Color.BLACK);
			}
		}
	}
	
	// Start Screen
	public static void main(String[] args) throws Exception {
		Thread u = new sound();
		u.start();
		Main main = new Main();
		new Thread(main).start();
	}	
	public Main() throws Exception {
	 	startF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startF.setSize(518, 348);
		startF.setVisible(true);
		  
		startP.setLayout(null);
		startF.add(startP);
		
		ImageIcon icon = new ImageIcon("data/Cover.jpg");
	 	JLabel startL = new JLabel(icon);
		startL.setBounds(0,  0, 500, 300);
	 	startP.setLayer(startL, 0);
		startP.add(startL);
		
		startB.setBounds(185, 150, 170, 60);
		startB.setFont(new Font("Arial", Font.BOLD, 50));
		startB.setForeground(Color.BLACK); 
		startB.setBackground(Color.WHITE);
		startB.setOpaque(false);
		startB.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				control();
		    }
		});
		startP.setLayer(startB, 1);
		startP.add(startB);
       
	}
	
	public void control() {
		Thread u = new controlChecker();
		u.start();
		startF.setVisible(false);
		
		controlF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	controlF.setSize(518, 348);
	 	controlF.setVisible(true);

		controlP.setLayout(null);
		controlF.add(controlP);
		
		//Background
		ImageIcon icon = new ImageIcon("data/CoverBlank.jpg");
	 	controlBack = new JLabel(icon);
	 	controlBack.setBounds(0,  0, 500, 300);
	 	controlP.setLayer(controlBack, 0);
	 	controlP.add(controlBack);
        
	 	//P1 ready button
		controlB1.setBounds(60, 100, 250, 80);
		controlB1.setFont(new Font("Arial", Font.BOLD, 20));
		controlB1.setBackground(Color.BLUE);
		controlB1.setForeground(Color.WHITE);
		controlB1.addActionListener(new controlP1());
        controlP.setLayer(controlB1, 2);
	    controlP.add(controlB1);

	    //P2 ready button
		controlB2.setBounds(60, 200, 250, 80);
		controlB2.setFont(new Font("Arial", Font.BOLD, 20));
		controlB2.setBackground(Color.RED);
		controlB2.setForeground(Color.WHITE);
		controlB2.addActionListener(new controlP2());
        controlP.setLayer(controlB2, 3);
		controlP.add(controlB2);
		
		//Back arrow
		BufferedImage logo = null;
		try {logo = ImageIO.read(new File("data/arrow.png"));} catch (IOException e1) {e1.printStackTrace();}
        controlB3 = new JButton(new ImageIcon(logo));
        controlB3.setBounds(30, 25, logo.getWidth(), logo.getHeight());
        controlB3.addActionListener(new Back());
        controlP.setLayer(controlB3, 1);
        controlP.add(controlB3);
		
        //Button to control screen
		controlB4.setBounds(100, 25, 120, 60);
		controlB4.setFont(new Font("Arial", Font.BOLD, 20));
		controlB4.setBackground(Color.WHITE);
		controlB4.setForeground(Color.BLACK);
		controlB4.addActionListener(new controlsInfo());
		controlB4.setFocusable(false);
        controlP.setLayer(controlB4, 4);
		controlP.add(controlB4);
		
		controlF.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int key = evt.getKeyCode();
				if(key == KeyEvent.VK_Q) {
					P1Ready = true;
					controlB1.setEnabled(false);
				}
				if(key == KeyEvent.VK_M) {
					P2Ready = true;
					controlB2.setEnabled(false);
				}
			}
		});
		controlF.createBufferStrategy(2);
		bufferStrategy = controlF.getBufferStrategy();
		controlF.requestFocus();
	}
	
	public void controlInfo() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(FRAMEWIDTH,FRAMEHEIGHT);
        
        JLayeredPane panel = new JLayeredPane();
        panel.setLayout(null);
        frame.add(panel);
        
        ImageIcon icon = new ImageIcon("data/CoverBlank.jpg");
	 	JLabel background = new JLabel(icon);
		background.setBounds(0,  0, 500, 300);
	 	panel.setLayer(background, 0);
		panel.add(background);
        
		JButton back = new JButton();
		BufferedImage logo = null;
		try {logo = ImageIO.read(new File("data/arrow.png"));} catch (IOException e1) {e1.printStackTrace();}
        back = new JButton(new ImageIcon(logo));
        back.setBounds(30, 20, logo.getWidth(), logo.getHeight());
        back.setFocusable(false);
        panel.setLayer(back, 1);
        panel.add(back);
		back.addActionListener(new controlInfoBack());
		
        JLabel label = new JLabel("PLAYER 1:");
        label.setFont(mainFont);
        label.setForeground(P1Color);
        label.setBounds(mainRowStart, 5, FRAMEWIDTH, 50);
	 	panel.setLayer(label, 1);
        panel.add(label);
		
        JLabel label2 = new JLabel("Move Up: E");
        label2.setFont(subFont);
        label2.setBounds(subRowStart, 5, FRAMEWIDTH, 50);
	 	panel.setLayer(label2, 2);
        panel.add(label2);
        
        JLabel label3 = new JLabel("Move Down: D");
        label3.setFont(subFont);
        label3.setBounds(subRowStart, 25, FRAMEWIDTH, 50);
	 	panel.setLayer(label3, 3);
        panel.add(label3);
        
        JLabel label4 = new JLabel("Move Left: S");
        label4.setFont(subFont);
        label4.setBounds(subRowStart, 45, FRAMEWIDTH, 50);
	 	panel.setLayer(label4, 4);
        panel.add(label4);
        
        JLabel label5 = new JLabel("Move Right: F");
        label5.setFont(subFont);
        label5.setBounds(subRowStart, 65, FRAMEWIDTH, 50);
	 	panel.setLayer(label5, 5);
        panel.add(label5);
        
        JLabel xlabel5 = new JLabel("Pick Up Snowball: Q");
        xlabel5.setFont(subFont);
        xlabel5.setBounds(subRowStart, 85, FRAMEWIDTH, 50);
	 	panel.setLayer(xlabel5, 6);
        panel.add(xlabel5);
	

	
        JLabel label6 = new JLabel("PLAYER 2:");
        label6.setFont(mainFont);
        label6.setForeground(P2Color);
        label6.setBounds(mainRowStart, 120, FRAMEWIDTH, 50);
	 	panel.setLayer(label6, 7);
        panel.add(label6);
		
        JLabel label7 = new JLabel("Move Up: ");
        label7.setFont(subFont);
        label7.setBounds(subRowStart, 120, FRAMEWIDTH, 50);
	 	panel.setLayer(label7, 8);
        panel.add(label7);
        
        JLabel label8 = new JLabel("Move Down: ");
        label8.setFont(subFont);
        label8.setBounds(subRowStart, 140, FRAMEWIDTH, 50);
	 	panel.setLayer(label8, 9);
        panel.add(label8);
        
        JLabel label9 = new JLabel("Move Left: ");
        label9.setFont(subFont);
        label9.setBounds(subRowStart, 160, FRAMEWIDTH, 50);
	 	panel.setLayer(label9, 10);
        panel.add(label9);
        
        JLabel label10 = new JLabel("Move Right: ");
        label10.setFont(subFont);
        label10.setBounds(subRowStart, 180, FRAMEWIDTH, 50);
	 	panel.setLayer(label10, 11);
        panel.add(label10);
	
        JLabel label11 = new JLabel("\u2191");
        label11.setFont(arrowFont);
        label11.setBounds(357, 114, FRAMEWIDTH, 50);
	 	panel.setLayer(label11, 12);
        panel.add(label11);
        
        JLabel label12 = new JLabel("\u2193");
        label12.setFont(arrowFont);
        label12.setBounds(378, 136, FRAMEWIDTH, 50);
	 	panel.setLayer(label12, 13);
        panel.add(label12);
        
        JLabel label13 = new JLabel("\u2190");
        label13.setFont(arrowFont);
        label13.setBounds(365, 158, FRAMEWIDTH, 50);
	 	panel.setLayer(label13, 14);
        panel.add(label13);
        
        JLabel label14 = new JLabel("\u2192 ");
        label14.setFont(arrowFont);
        label14.setBounds(375, 178, FRAMEWIDTH, 50);
	 	panel.setLayer(label14, 15);
        panel.add(label14);
     
        JLabel label15 = new JLabel("Pick Up Snowball: M ");
        label15.setFont(subFont);
        label15.setBounds(subRowStart, 200, FRAMEWIDTH, 50);
	 	panel.setLayer(label15, 16);
        panel.add(label15);
	
        JLabel label16 = new JLabel("=   Power Up (Increases speed of player briefly)");
        label16.setFont(subFont);
        label16.setBounds(61, 244, FRAMEWIDTH, 50);
	 	panel.setLayer(label16, 17);
        panel.add(label16);
     
        JTextArea box = new JTextArea();
        box.setBackground(Color.GREEN);
        box.setBounds(30, 260, 18, 18);
	 	panel.setLayer(box, 18);
        panel.add(box);
     
        frame.setVisible(true);	
	}
	
	public class controlInfoBack implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			P1Ready = false;
			P2Ready = false;
			frame.setVisible(false);
			controlF.setVisible(true);
			controlB1.setEnabled(true);
			controlB2.setEnabled(true);
		}	
	}
	
	public class Back implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			P1Ready = false;
			P2Ready = false;
			controlF.setVisible(false);
			startF.setVisible(true);
			controlB1.setEnabled(true);
			controlB2.setEnabled(true);
		}
	}
	
	public class controlP1 implements ActionListener {//the action that occurs when p1 presses the ready button
		public void actionPerformed(ActionEvent e) {
			P1Ready = true;
			controlB1.setEnabled(false);
		}
	}
	public class controlP2 implements ActionListener {//the thing that happens when p2 presses ready
		public void actionPerformed(ActionEvent e) {
			P2Ready = true;
			controlB2.setEnabled(false);
		}
	}
	public class controlsInfo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			P1Ready = false;
			P2Ready = false;
			controlB1.setEnabled(true);
			controlB2.setEnabled(true);
			controlF.setVisible(false);
			controlInfo();
		}
	}
	private class controlChecker extends Thread {//this keeps on running and checks to see if both buttons are pressed
		private boolean check = true;
		public void run() {
			while (check) {
				System.out.println("");
				if (P1Ready && P2Ready) {
					check = false;
					controlF.dispose();
					try {
						game();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static class sound extends Thread {
		public void run() {
			try {
				Clip clip;
			    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("data/MUSAC.wav").getAbsoluteFile());
		        clip = AudioSystem.getClip();
		        clip.open(audioInputStream);
		        clip.loop(9999);
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	// Game Screen
	public void game() throws IOException {	
		Game ex = new Game();
		new Thread(ex).start();
	}
}