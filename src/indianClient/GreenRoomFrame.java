package indianClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class GreenRoomFrame extends JFrame{
	private BufferedImage img;
	private JLayeredPane lp;
	private JLabel makeroom, refresh;
	private File bgm;
	private Clip clip;
	private Vector data;
	private JTable table;
	private JButton btnRoomCreate, btnRefresh;
	
	public GreenRoomFrame() {
		super("Indian Poker");
		bgm = new File("rsc/list.wav");
		try {
		clip = AudioSystem.getClip();
		clip.open(AudioSystem.getAudioInputStream(bgm));
		clip.start();
		clip.loop(3);
		}catch (Exception e) {
			
		}
		data = Connect.getRoomList();
		showScreen();
		
		
	}
	
	void showScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocation(screenSize.width/2 - this.getSize().width/2 - 550, screenSize.height/2 - 350 - this.getSize().height/2);
		this.setSize(1200,725);
		this.setResizable(false);
		this.setLayout(null);
		//this.setUndecorated(true);
		
		
		
		lp = new JLayeredPane();
		lp.setBounds(0,0,1200,700);
		lp.setLayout(null);
		
		try {
			img = ImageIO.read(new File("rsc/greenroom.png"));
		} catch (IOException e) {
		}
		BackImg backimg = new BackImg();
		backimg.setBounds(0, 0, 1200, 700);
		
		Vector<String> col = new Vector<String>();
		col.add("방 번호");
		col.add("방 이름");
		col.add("방장 ");
		col.add("인원 ");
		col.add("방 상태");
		
		DefaultTableModel model = new DefaultTableModel(data,col) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		jTableSet();
		table.setBounds(147,30,850,700);
		table.addMouseListener(new MouseClick());;
		
		lp.add(table);
		
		btnRoomCreate = new JButton(new ImageIcon("rsc/roomCreates.png"));		
		btnRoomCreate.setBounds(1000,650, 85, 55);
		btnRoomCreate.setFocusPainted(false);
		btnRoomCreate.setContentAreaFilled(false);
		btnRoomCreate.setBorderPainted(false);
		btnRoomCreate.addActionListener(new ClickListener());
		lp.add(btnRoomCreate);
		
		makeroom = new JLabel("방 만들기");
		makeroom.setBounds(1070, 645, 100, 70);
		makeroom.setFont(new Font("serif", Font.BOLD, 15));
		makeroom.setForeground(Color.CYAN);
		lp.add(makeroom);
		
		btnRefresh = new JButton(new ImageIcon("rsc/refresh.png"));
		btnRefresh.setBounds(1000,550, 85, 55);
		btnRefresh.setContentAreaFilled(false);
		btnRefresh.setBorderPainted(false);
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Connect.reFresh();
				clip.close();
				lp.removeAll();
				dispose();
				GreenRoomFrame re = new GreenRoomFrame();	
			}
		});
		lp.add(btnRefresh);
		
		refresh = new JLabel("새 로 고 침");
		refresh.setBounds(1070, 550, 100, 50);
		refresh.setFont(new Font("serif", Font.BOLD, 15));
		refresh.setForeground(Color.CYAN);
		lp.add(refresh);
		
		lp.add(backimg);
		this.add(lp);
		this.setVisible(true);
	}
	
	public void jTableSet() {
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		DefaultTableCellRenderer celAlignCenter = new DefaultTableCellRenderer();   

	     celAlignCenter.setHorizontalAlignment(JLabel.CENTER);//가운데정렬
		//DefaultTableCellRenderer celAlignCenter = new DefaultTableCellRenderer();
		//table.getColumnModel().getColumn(0).setCellRenderer(celAlignCenter);
		for(int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(10);
			table.getColumnModel().getColumn(i).setCellRenderer(celAlignCenter);
		}
		table.setBackground(Color.lightGray);
		table.setOpaque(false);
		table.setRowHeight(35);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setForeground(Color.BLUE);
	}

	private class BackImg extends JPanel{
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}
	
	private class ClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(Connect.roomCreate()) {
				clip.close();
				lp.removeAll();
				dispose();
				GameTableFrame go = new GameTableFrame();
			}else {
				clip.close();
				lp.removeAll();
				dispose();
				GreenRoomFrame re = new GreenRoomFrame();
			}
			
		}
	}
	
	private class MouseClick implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			int index = table.getSelectedRow();
			System.out.println("클릭이벤트 됨" + index);
			if(index > -1) {
				int roomid = (int) table.getValueAt(index, 0);
				if(roomid != 0) {
					boolean retval = Connect.joinRoom(roomid);	
					clip.close();
					lp.removeAll();
					dispose();
					if(retval == true) {
						GameTableFrame go = new GameTableFrame();
					}
					else {
						GreenRoomFrame re = new GreenRoomFrame();
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}	
	}
}
