package indianClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class GameTableFrame extends JFrame implements Runnable {
	private BufferedImage img;
	private JRootPane rootPane;
	private JLayeredPane lp;
	private File bgm;
	private Clip clip;
	ImageIcon callBtn, callHover, callClick, betBtn, betHover, betClick, dieBtn, dieHover, dieClick, submitBtn;
	protected JButton betting, call, die, send;
	private JScrollPane chat;
	private JTextArea chatview;
	private JTextField text;
	private ImageIcon[] card;
	private JLabel msg, p1card, p2card, p1name, p2name, p1record, p2record, p1garnet, p2garnet, sumbetting, curbetting;
	private boolean owner, flagdie, flagword = false;
	private int myGarnet = 20 , betvalue = 0, betsum = 0;
	private String sendop = "" , bettingvalue, bettingsum, garnet, sendmsg;
	
	public GameTableFrame() {
		super("Indian Poker");
		/*
		 * bgm = new File("rsc/maple.wav"); try { clip = AudioSystem.getClip();
		 * clip.open(AudioSystem.getAudioInputStream(bgm)); clip.start(); clip.loop(3);
		 * }catch(Exception e) {e.printStackTrace();}
		 */
		showScreen();
		new Thread(this).start();
	}

	public void showScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocation(screenSize.width / 2 - this.getSize().width / 2 - 550,
				screenSize.height / 2 - 350 - this.getSize().height / 2);
		this.setSize(1200, 730);
		this.setResizable(false);
		this.setLayout(null);
		// this.setUndecorated(true);
		callBtn = new ImageIcon("rsc/CallButton.png");
		callHover = new ImageIcon("rsc/CallButton_hover.png");
		callClick = new ImageIcon("rsc/CallButton_pushed.png");

		betBtn = new ImageIcon("rsc/BetButton.png");
		betHover = new ImageIcon("rsc/BetButton_hover.png");
		betClick = new ImageIcon("rsc/BetButton_pushed.png");

		dieBtn = new ImageIcon("rsc/DieButton.png");
		dieHover = new ImageIcon("rsc/DieButton_hover.png");
		dieClick = new ImageIcon("rsc/DieButton_pushed.png");
		
		submitBtn = new ImageIcon("rsc/SubmitButton.png");

		card = new ImageIcon[10]; //1~10번 카드 이미지 미리로딩
		for(int i = 1; i <= 10; i ++) {
			String filename = "rsc/" + i +".png";
			card[i-1] = new ImageIcon(filename);
		}
		
		lp = new JLayeredPane();
		lp.setBounds(0,0,1200,700);
		lp.setLayout(null);
		
		try {
			img = ImageIO.read(new File("rsc/desk3.png"));
		}catch (IOException e) {}
		
		BackImg back = new BackImg();
		back.setBounds(0,0,1200,700);
		betting = new JButton("",betBtn);
		betting.setPressedIcon(betClick); // pressedIcon용 이미지 등록
		betting.setRolloverIcon(betHover); // rolloverIcon용 이미지 등록
		betting.setBorderPainted(false); //테두리 없애기
		betting.setContentAreaFilled(false);
		betting.setFocusPainted(false);

		betting.setBounds(10, 620, 90, 60);
		betting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bgm = new File("rsc/betsound.wav");
				try {
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(bgm));		
				clip.start();				
				}
				catch (Exception e1) {e1.printStackTrace();}
				String get = JOptionPane.showInputDialog("배팅 갯수를 입력하세요 : ");
				int g = Integer.parseInt(get) + betvalue;
				System.out.println("내가 입력한 값 : "+ g);
				String get2 = String.valueOf(g);
				if(g > myGarnet)
					JOptionPane.showMessageDialog(null, "보유 가넷을 초과했습니다");
				else {
					sendop = "betting";
					betvalue = g;
					betsum += g;
					myGarnet -= g;
					setBetting(get, get2);
					updateMyGarnet(myGarnet);
					updateBettingvalue(betvalue);
					updateBetSum(betsum);
					setDisable();
					System.out.println("set myop : " + sendop + "betvalue  : " + betvalue
							+ " betSum :" + betsum + " myGarnet" + myGarnet + ", string betting : " + getBettingString());
				}
			}

		});
		lp.add(betting);
		
		call = new JButton("",callBtn);
		call.setPressedIcon(callClick); // pressedIcon용 이미지 등록
		call.setRolloverIcon(callHover); // rolloverIcon용 이미지 등록
		call.setBorderPainted(false);
		call.setContentAreaFilled(false);
		call.setFocusPainted(false);
		
		call.setBounds(110, 620, 90, 60);
		call.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bgm = new File("rsc/callsound.wav");
				try {
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(bgm));		
				clip.start();				
				}
				catch (Exception e1) {e1.printStackTrace();}
				
				sendop = "call";
				setDisable();
				changeMsg("결과를 확인 합니다.");
			}
		});
		lp.add(call);
		
		die = new JButton("",dieBtn);
		die.setPressedIcon(dieClick); // pressedIcon용 이미지 등록
		die.setRolloverIcon(dieHover); // rolloverIcon용 이미지 등록
		die.setBorderPainted(false);
		die.setContentAreaFilled(false);
		die.setFocusPainted(false);

		die.setBounds(210, 620, 90, 60);
		die.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bgm = new File("rsc/diesound.wav");
				try {
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(bgm));		
				clip.start();				
				}
				catch (Exception e1) {e1.printStackTrace();}
				sendop = "die";
				setDisable();
				changeMsg("결과를 확인합니다");
				
			}
		});
		lp.add(die);
		
		chatview = new JTextArea();
		chatview.setEditable(false);
		text = new JTextField();
		chat = new JScrollPane(chatview, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		text.setBounds(365, 650, 400, 30);
		//chatview.setOpaque(false);
		//chat.setOpaque(false);
		//chat.getViewport().setOpaque(false);
		chat.setBounds(365, 465, 465, 180);
		send = new JButton("",submitBtn);
		send.setBounds(765, 650, 65, 30);
		send.setBorderPainted(false);
		send.setContentAreaFilled(false);
		send.setFocusPainted(false);
		
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String my;
				if(isOwner()) my = p1name.getText();
				else my = p2name.getText();
				
				sendmsg = my + " : " + text.getText();
				chatview.setText(chatview.getText() + "\n" + sendmsg);
				flagword = true;
				
				text.setText("");
				repaint();
			}
		});
		lp.add(chat);
		lp.add(text);
		lp.add(send);
		
		//상태메시지
		msg = new JLabel("상대를 기다리는중...");
		msg.setBounds(400, 40, 520, 130);
		msg.setFont(new Font("serif ", Font.BOLD, 30));
		msg.setForeground(Color.RED);
		lp.add(msg);
		
		//카드
		p1card = new JLabel("");
		p1card.setBounds(100,37,117,163);
		p2card = new JLabel("");
		p2card.setBounds(975,37,117,163);
		lp.add(p1card);
		lp.add(p2card);
		
		//가넷갯수
		p1garnet = new JLabel("x 20");
		p2garnet = new JLabel("x 20");
		sumbetting = new JLabel("x 0");
		curbetting = new JLabel("현재 배팅 x 0");
		p1garnet.setForeground(Color.cyan);
		p2garnet.setForeground(Color.cyan);
		sumbetting.setForeground(Color.RED);
		curbetting.setForeground(Color.RED);
		p1garnet.setFont(new Font("serif ", Font.PLAIN, 20));
		p2garnet.setFont(new Font("serif ", Font.PLAIN, 20));
		sumbetting.setFont(new Font("serif ", Font.PLAIN, 32));
		curbetting.setFont(new Font("serif ", Font.PLAIN, 24));
		p1garnet.setBounds(60,550,50,50);
		p2garnet.setBounds(950,555,50,50);
		sumbetting.setBounds(595, 240, 70, 70);
		curbetting.setBounds(920, 650, 220, 50);
		lp.add(p1garnet);
		lp.add(p2garnet);
		lp.add(sumbetting);
		lp.add(curbetting);
		
		//이름과 전적
		p1name = new JLabel("");
		p2name = new JLabel("");
		p1name.setBounds(30,450,200,40);
		p2name.setBounds(910,450,200,40);
		p1name.setFont(new Font("serif", Font.BOLD, 16));
		p1name.setForeground(Color.YELLOW);
		p2name.setFont(new Font("serif", Font.BOLD, 16));
		p2name.setForeground(Color.YELLOW);
		lp.add(p1name);
		lp.add(p2name);
			
		rootPane = this.getRootPane();
		rootPane.setDefaultButton(send);
		
		lp.add(back);
		add(lp);
		setVisible(true);
		betting.setEnabled(false);
		call.setEnabled(false);
		die.setEnabled(false);
	}

	public synchronized void initCardView() {
		p1card.setIcon(null);
		p2card.setIcon(null);
	}																									

	public synchronized void setP1Card(int n) {
		p1card.setIcon(card[n - 1]);
		revalidate();
		repaint();
	}

	public synchronized void setP2Card(int n) {
		p2card.setIcon(card[n - 1]);
		revalidate();
		repaint();
	}

	public synchronized  void setOtherCard(int n) {
		if (owner)
			p2card.setIcon(card[n - 1]);
		else
			p1card.setIcon(card[n - 1]);
		revalidate();
		repaint();
	}
	
	public synchronized void setMyCard(int n) {
		if(owner)
			p1card.setIcon(card[n-1]);
		else
			p2card.setIcon(card[n-1]);
		revalidate();
		repaint();
		
	}

	public synchronized void setDisable() {
		betting.setEnabled(false);
		call.setEnabled(false);
		die.setEnabled(false);
		msg.setText("상대방 차례입니다.");
		revalidate();
		repaint();
	}

	public synchronized void setEnable() {
		if(myGarnet < 1) betting.setEnabled(false);
		else betting.setEnabled(true);
		if(betvalue != 0 || myGarnet < 1) { call.setEnabled(true);  } 
		else call.setEnabled(false);
		die.setEnabled(true);
		msg.setText("내 차례입니다.");
		revalidate();
		repaint();
	}

	public synchronized void getUserName(String s) {
		String t = s.substring(0, 4);
		if (t.equals("true"))
			owner = true;
		else
			owner = false;
		int idx = s.indexOf("ID : ");
		s = s.substring(idx + 5);
		idx = s.indexOf(", P2 ");
		String p1n = s.substring(0, idx);
		idx = s.indexOf("ID : ");
		String p2n = s.substring(idx + 5);
	
		p1name.setText(p1n);
		p2name.setText(p2n);
		
		revalidate();
		repaint();
	}
	
	public synchronized void changeMsg(String s) {
		msg.setText(s);
		revalidate();
		repaint();
	}
	
	public synchronized void updateBettingvalue(int n) {
		betvalue = n;
		String str = "배팅 가넷 x " + String.valueOf(n);
		curbetting.setText(str);
		revalidate();
		repaint();
	}
	
	public synchronized void updateBetSum(int n) {
		betsum = n;
		sumbetting.setText("x "+String.valueOf(betsum));
		revalidate();
		repaint();
	}

	public synchronized void updateMyGarnet(int n) {
		myGarnet = n;
		String str = "x " + String.valueOf(n);
		if(owner)
			p1garnet.setText(str);
		else
			p2garnet.setText(str);
		revalidate();
		repaint();
	}
	public synchronized void updateGarnet(int n) {
		String str ="x " + String.valueOf(n);
		if (owner)
			p2garnet.setText(str);
		else
			p1garnet.setText(str);
		revalidate();
		repaint();
	}
	
	public synchronized void setMyop(String s) {
		sendop = s;
	}
	
	public synchronized String getMyOp() {
		return sendop;
	}
	
	public synchronized void setBetvalue(int n) {
		betvalue = n;
	}
	
	public synchronized int getBetvalue() {
		return betvalue;
	}
	
	public synchronized boolean isOwner() {
		return owner;
	}
	
	public synchronized void setBetting(String bet, String betsum) {
		bettingvalue = bet;
		bettingsum = betsum;
	}
	
	public synchronized String getBettingString() {
		return bettingvalue;
	}
	
	public synchronized String getBettingSumString() {
		return bettingsum;
	}
	
	public synchronized boolean getDie() {
		return flagdie;
	}
	
	public synchronized void setDie(boolean flag) {
		flagdie = flag;
	}
	
	public synchronized boolean getWord() {
		return flagword;
	}
	
	public synchronized void setWord(boolean flag) {
		flagword = flag;
	}
	
	public synchronized String getSendMsg() {
		return sendmsg;
	}
	
	public synchronized void addChatting(String str) {	
		chatview.setText(chatview.getText() + "\n" + str);

		repaint();
	}
	
	public synchronized int getMyGarnet() {
		return myGarnet;
	}
	
	private class BackImg extends JPanel {
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}
	
	@Override
	public void run() {
		Connect.gameStart(this);
		dispose();
		GreenRoomFrame gameEnd = new GreenRoomFrame();
	}
}
