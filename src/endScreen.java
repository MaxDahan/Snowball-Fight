import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/**
 * @author Maxime Dahan
 */
public class endScreen{
	
	private BufferStrategy bufferStrategy;
	private JFrame endFrame = new JFrame("End Screen");
	private JPanel endPanel = new JPanel();
	private JLabel winner;
	private JTextArea podiumP1 = new JTextArea();
	private JTextArea podiumP2 = new JTextArea();
	private JButton restart = new JButton("Restart");
	
	private JTextArea p1st = new JTextArea();
	private JTextArea p2nd = new JTextArea();
	
	private JLabel winnerPt2 = new JLabel(" Won!");
	private boolean win; //true is player1 won and false is player 2 won
	
	public static void main(String[] args) {
		new endScreen(true);
	}
	
	public void RestartCheck(KeyEvent evt) throws IOException {
		int key = evt.getKeyCode();
		if(key == KeyEvent.VK_R) {
			endFrame.dispose();
			Game game = new Game();
			new Thread(game).start();
		}
	}
	
	public endScreen(boolean w) {
		p1st.setEditable(false);
		p2nd.setEditable(false);
		podiumP1.setEditable(false);
		podiumP2.setEditable(false);
		
		win = w;
		endFrame.setResizable(false);
		endFrame.setSize(500, 500);
		endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		endPanel.setLayout(null);
		endFrame.add(endPanel);
		
		if(win) {
			winner = new JLabel("Player 1");
		}else if(!win) {
			winner = new JLabel("Player 2");
		}

		winner.setFont(new Font("Arial", Font.BOLD, 60));
		winner.setForeground(Color.BLACK);
		winner.setBounds(30, 20, 400, 80);
		endPanel.add(winner);
		winner.repaint();

		restart.setFont(new Font("Arial", Font.BOLD, 25));
		restart.setForeground(Color.BLACK);
		restart.setBackground(Color.WHITE);
		restart.setBounds(260, 380, 130, 50);
		endPanel.add(restart);
		restart.addActionListener(new restart());
		
		/*JLabel pod1 = new JLabel("1st");
		pod1.setFont(new Font("Arial", Font.BOLD, 80));
		pod1.setForeground(Color.WHITE);
		pod1.setBounds(260, 300, 150, 80);
		endPanel.add(pod1);
		pod1.repaint();*/
		
		/*JLabel pod2 = new JLabel("2nd");
		pod2.setFont(new Font("Arial", Font.BOLD, 50));
		pod2.setForeground(Color.WHITE);
		pod2.setBounds(75, 350, 150, 80);
		endPanel.add(pod2);
		pod2.repaint();*/
		
		if(win) {
			p1st.setBackground(Color.BLUE);
		}else {
			p1st.setBackground(Color.RED);
		}
		p1st.setBounds(262, 140, 130, 130);
		endPanel.add(p1st);
		
		if(win) {
			p2nd.setBackground(Color.RED);
		}else {
			p2nd.setBackground(Color.BLUE);
		}
		p2nd.setBounds(80, 270, 80, 80);
		endPanel.add(p2nd);
		
		podiumP1.setBackground(Color.BLACK);
		podiumP1.setBounds(60, 350, 120, 150);
		endPanel.add(podiumP1);
		
		podiumP2.setBackground(Color.BLACK);
		podiumP2.setBounds(240, 270, 170, 220);
		endPanel.add(podiumP2);
		
		winnerPt2.setFont(new Font("Arial", Font.BOLD, 60));
		winnerPt2.setForeground(Color.BLACK);
		winnerPt2.setBounds(270, 20, 200, 80);
		endPanel.add(winnerPt2);
		
		endFrame.setVisible(true);
		
		endFrame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				try {
					RestartCheck(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		endFrame.createBufferStrategy(2);
		bufferStrategy = endFrame.getBufferStrategy();
		endFrame.requestFocus();
		
	}
	
	class restart implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			endFrame.dispose();
			Game game = null;
			try {
				game = new Game();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			new Thread(game).start();
		}
	}
}
