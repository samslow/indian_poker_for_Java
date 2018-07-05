package indianClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame{
	private JRootPane rootPane;
	private JPanel imgPanel, idPanel, pwdPanel;
	private JTextField id;
	private JPasswordField pwd; 
	private JButton btnLogin;
	private BufferedImage img;
	private JLayeredPane layeredPane;
	private File bgm;
	private Clip clip;
	
	public LoginFrame()  {
		super("Login");		
		bgm = new File("rsc/title.wav");
		try {
		clip = AudioSystem.getClip();
		clip.open(AudioSystem.getAudioInputStream(bgm));		
		clip.start();
		clip.loop(3);
		}catch (Exception e) {
			System.out.println("bgm error");
		}
		showScreen();
	}
	
	public void showScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocation(screenSize.width/2 - this.getSize().width/2 - 550, screenSize.height/2 - 350 - this.getSize().height/2);
		this.setSize(1200, 750);
		this.setResizable(false);
		this.setTitle("Login");
		this.setLayout(null);
		//this.setUndecorated(true);
		
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(0,0,1200,750);
		layeredPane.setLayout(null);
		//background img		
		try {
			img = ImageIO.read(new File("rsc/title3.png"));
		} catch (IOException e) {
		}
		BackImg backimg = new BackImg();
		backimg.setBounds(0, 0, 1200, 750);
		
		//id
		id = new JTextField("",15);
		id.setBounds(490,553,150,30);
		id.setOpaque(false);
		id.setForeground(Color.BLACK);
		id.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		layeredPane.add(id);

		//pwd
		pwd = new JPasswordField("",15);
		pwd.setBounds(490,610,150,30);
		pwd.setOpaque(false);
		pwd.setForeground(Color.BLACK);
		pwd.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		layeredPane.add(pwd);
		
		//btn
		btnLogin = new JButton(new ImageIcon("rsc/btnLogin.png"));
		btnLogin.setBounds(535,660,110,55);
		btnLogin.setBorderPainted(false);
		btnLogin.setFocusPainted(false);
		btnLogin.setContentAreaFilled(false);
		btnLogin.addActionListener(new ClickListener());
		layeredPane.add(btnLogin);
		
		//enter로 로그인
		rootPane = this.getRootPane();
		rootPane.setDefaultButton(btnLogin); 
		
		layeredPane.add(backimg);
		this.add(layeredPane);
		this.setVisible(true);
		
		
	}
	
	public boolean procLogin() {
		String sendid = id.getText();
		String sendpwd = pwd.getText();
		
		if(sendid.equals("")|| sendpwd.equals("")) return false;
		
		Connect.serverConn(sendid, sendpwd);
		if(Connect.getFlag() == true) return true;
		else return false;
	}
	
	
	private class ClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(procLogin()) {//접속이 된경우 방 리스트 화면으로 넘어가자
				clip.close();
				layeredPane.removeAll();
				dispose();
				GreenRoomFrame go = new GreenRoomFrame();
			}else {
				JOptionPane.showMessageDialog(null, "아이디 혹은 패스워드를 잘못 입력하셨습니다.");
				id.setText("");
				pwd.setText("");
			}
		}
	}
	
	private class BackImg extends JPanel{
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}
}

