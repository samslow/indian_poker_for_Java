package indianClient;

import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Connect {
	private static String IP = "114.70.235.168";
	//final static String IP = "127.0.0.1";
	final static int PORT = 9977;
	static Socket socket;
	static OutputStream out;
	static DataOutputStream dout;
	static InputStream in;
	static DataInputStream din;
	static ObjectInputStream oin;
	static ObjectOutputStream oout;

	static boolean flag = false;

	public static void serverConn(String id, String pwd) {
		try {
			socket = new Socket(IP, PORT);

			out = socket.getOutputStream();
			dout = new DataOutputStream(out);

			in = socket.getInputStream();
			din = new DataInputStream(in);

			dout.writeUTF(id);
			dout.writeUTF(pwd);
			String ok = din.readUTF();
			if (ok.equals("OK"))
				flag = true;
			System.out.println(flag);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "서버에 접속할 수 없습니다.", "접속에러", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector getRoomList() {
		Vector data = new Vector();

		try {
			
			int count = din.readInt();
			//System.out.println("##" + count);

			for (int i = 0; i < count; i++) {
				int id = din.readInt();
				String roomName = din.readUTF();
				String owner = din.readUTF();
				int num = din.readInt();
				String status = num==1? "대기 중" : "진행 중";
				Vector row = new Vector();
				row.add(id);
				row.add(roomName);
				row.add(owner);
				row.add(num);
				row.add(status);
				data.add(row);
				System.out.println(id + " " + roomName + " " + owner + " " + num + " " + status );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void reFresh() {
		try {
			dout.writeUTF("refresh");
		}catch(IOException e) {}
	}
	
	public static boolean roomCreate() {
		String name = JOptionPane.showInputDialog("방이름을 입력하세요 : ");
		System.out.println(name);
		if (name == null)
			return false;
		String send = "RoomCreate";
		try {
			dout.writeUTF(send);
			dout.writeUTF(name);
			String retval = din.readUTF();
			if (retval.equals("RCS"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean joinRoom(int roomid) {
		String send = "RoomJoin";
		try {
			dout.writeUTF(send);
			dout.writeInt(roomid);
			String retval = din.readUTF();
			if (retval.equals("RJS"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean gameStart(GameTableFrame game) {
		try {
			Robot r = new Robot();
			//String 
			String wait = din.readUTF();
			while (wait.equals("wait")) {
				r.delay(2000);
				wait = din.readUTF();				
			}
			System.out.println("방장 탈출!");
			
					
			game.changeMsg("게임을 시작합니다.");
			new Thread(new Chat(game)).start();		
			
			String gameInfo = din.readUTF();
			game.getUserName(gameInfo);
			System.out.println(gameInfo);
			r.delay(2500);

			game.changeMsg("선을 결정합니다.");
			int p1Card, p2Card;

			p1Card = din.readInt();
			p2Card = din.readInt();

			r.delay(2500);

			// 보여주기
			game.setP1Card(p1Card);
			game.setP2Card(p2Card);
			if (game.isOwner()) {
				if (p1Card > p2Card) {
					game.changeMsg("당신이 선입니다.");
					r.delay(2500);
					game.setEnable();
				} else {
					game.changeMsg("상대방이 선입니다.");
					r.delay(2500);
					game.setDisable();
				}
			} else {
				if (p1Card > p2Card) {
					game.changeMsg("상대방이 선입니다.");
					r.delay(2500);
					game.setDisable();
				} else {
					game.changeMsg("당신이 선입니다.");
					r.delay(2500);
					game.setEnable();
				}
			}

			// 배팅 프로세스
			String endsignal = din.readUTF();
			while (!endsignal.equals("Game Over")) {
				System.out.println("End signal: " + endsignal);
				game.initCardView();
				
				int myGarnet = din.readInt();
				game.updateMyGarnet(myGarnet);
				System.out.println("받은 가넷 : " + myGarnet);
				int otherGarnet = din.readInt(); // 상대 가넷 업데이트
				game.updateGarnet(otherGarnet);
				game.updateBetSum(din.readInt());
				game.updateBettingvalue(din.readInt());
				game.setDie(false);

				int otherCard = din.readInt();// 상대카드 받기
				System.out.println("this turn other card : " + otherCard);
				game.setOtherCard(otherCard);

				String betsignal = din.readUTF();
				do {
					System.out.println(betsignal);
					if (betsignal.equals("Your turn")) {
						game.setEnable();
						// 게임테이블에서 클릭 이벤트 발생 - 배팅 - 콜 - 다이
						String sendop = "";
						game.setMyop("");
						do {
							sendop = game.getMyOp();
							r.delay(1500);
						} while (sendop.equals(""));
						dout.writeUTF(sendop);

						if (sendop.equals("betting")) {
							game.setBetvalue(din.readInt());
							int n = Integer.parseInt(game.getBettingString());
							dout.writeInt(n);
							System.out.println("배팅 보냄 " + n + " " + game.getBettingString());
						} else if (sendop.equals("call")) {
							// 서버에서 처리된 가넷 결과 받아야함 배팅값 총배팅된값 내가넷
							game.updateBettingvalue(din.readInt());
							game.updateBetSum(din.readInt());
							game.updateMyGarnet(din.readInt());
						} else if (sendop.equals("die")) {// 무조건 패배
							game.setDie(true);
						}
						System.out.println("보냄" + sendop);
					} else if (betsignal.equals("Wait turn")) {
						game.setDisable();
						r.delay(3500);
						String otherOp = din.readUTF();
						game.updateBettingvalue(din.readInt()); // 배팅 가넷수
						game.updateGarnet(din.readInt()); // 상대방 가넷
						game.updateBetSum(din.readInt());

						System.out.println("상대가 배팅한 값 " + game.getBetvalue());

						if (otherOp.equals("betting"))
							game.setEnable();

					}
					betsignal = din.readUTF();
				} while (!betsignal.equals("Betting end"));
				// 결과 처리
				game.changeMsg("결과를 확인합니다.");
				r.delay(2000);
				String otherDie = din.readUTF(); //상대방 다이확인 				
				int myCard = din.readInt();
				game.setMyCard(myCard); // 내카드를 받아서 보여줌
				System.out.println(myCard + " ' " + otherCard + " . " + game.isOwner());

				if (!game.getDie() && otherDie.equals("false")) {
					if (myCard > otherCard) {
						game.changeMsg("승리!");
						r.delay(1500);
						game.setEnable();
						JOptionPane.showMessageDialog(null, "이겼습니다!");
					} else if (otherCard > myCard) {
						game.changeMsg("패배!");
						r.delay(1500);
						game.setDisable();
						JOptionPane.showMessageDialog(null, "졌습니다.");
					} else {
						game.changeMsg("무승부!");
						r.delay(1500);
						JOptionPane.showMessageDialog(null, "비겼습니다.");
					}
				}
				else if(otherDie.equals("true")) {
					game.changeMsg("승리!");
					r.delay(1500);
					JOptionPane.showMessageDialog(null, "상대방이 포기했습니다.");
				}
				else {
					game.changeMsg("포기!");
					r.delay(1500);
					game.setDisable();
					JOptionPane.showMessageDialog(null, "포기했습니다.");				
				}

				r.delay(2000);
				
				// 게임종료
				endsignal = din.readUTF();
			}
			game.changeMsg("게임이 종료되었습니다.");
			r.delay(2000);
			System.out.println("게임 끝");
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean getFlag() {
		return flag;
	}
}
