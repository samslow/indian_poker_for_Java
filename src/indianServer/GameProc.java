package indianServer;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.MalformedInputException;
import java.util.Random;

public class GameProc implements Runnable {
	private GameRoom procRoom;
	private GameUser p1, p2;
	private OutputStream out, out2;
	private DataOutputStream dout, dout2;
	private InputStream in, in2;
	private DataInputStream din, din2;
	private Socket sock, sock2;
	private boolean whoami = false;// ���屸��
	
	GameProc(GameUser user) {
		procRoom = user.getRoom();
		procRoom.setStatus(false);
		System.out.println(procRoom.getName() + " " + procRoom.getOwner() + " " + procRoom.getOther());
		if (user.equals(procRoom.getOwner())) {
			whoami = true;
			p1 = user;
			p2 = procRoom.getOther();			
		} else {
			whoami = false;
			p1 = procRoom.getOwner();
			p2 = user;
		}	
		this.run();	}

	@Override
	public void run() {
		sock = p1.getSocket();
		sock2 = p2.getSocket();
		try {
			in = sock.getInputStream();
			in2 = sock2.getInputStream();
			out = sock.getOutputStream();
			out2 = sock2.getOutputStream();
			din = new DataInputStream(in);
			din2 = new DataInputStream(in2);
			dout = new DataOutputStream(out);
			dout2 = new DataOutputStream(out2);			
			
			String info = whoami + procRoom.toString();
			System.out.println("������ ���۵Ǿ����ϴ� " + info);
					
			
			if (!whoami) {//2�� ������ϱ� �ϳ��� ä�ÿ� �ϳ��� ���ӿ����� �ϸ� �ɵ�!
				dout2.writeUTF(info);
				//System.out.println("ä���� ���۵˴ϴ�: " + cPort);
				
				boolean isEnd = false;
				
				ServerSocket chattingSocket = ServerMain.chattingSocket;
				Socket p1 = chattingSocket.accept();
				Socket p2 = chattingSocket.accept();
				
				DataOutputStream outp1 = new DataOutputStream(p1.getOutputStream());
				DataInputStream inp1 = new DataInputStream(p1.getInputStream());
				DataOutputStream outp2 = new DataOutputStream(p2.getOutputStream());
				DataInputStream inp2 = new DataInputStream(p2.getInputStream());			
				do {
					try {
						outp1.writeUTF("msgStart");
						outp2.writeUTF("msgStart");
						
						String p1msg = inp1.readUTF();
						String p2msg = inp2.readUTF();
						
						if(!p1msg.equals("null"))
							outp2.writeUTF(p1msg);
						if(!p2msg.equals("null"))
							outp1.writeUTF(p2msg);
						
					} catch (IOException e) {					
						e.printStackTrace();
						outp1.close();
						outp2.close();
						inp1.close();
						inp2.close();
						p1.close();
						p2.close();
						return;
					}
					
					isEnd = procRoom.getSatus();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (!isEnd);
				
				outp1.writeUTF("END");
				outp2.writeUTF("END");
			} else {//���ӿ�~				
				dout.writeUTF(info);
				System.out.println("������ ���۵˴ϴ�");
				// ������
				procRoom.initDeck();
				do {
					p1.setMyCard(procRoom.drawDeck());
					p2.setMyCard(procRoom.drawDeck());
				} while (p1.getMyCard() == p2.getMyCard());

				procRoom.whoFirst();
				dout.flush();
				dout2.flush();
				dout.writeInt(p1.getMyCard());
				dout.writeInt(p2.getMyCard());
				dout2.writeInt(p1.getMyCard());
				dout2.writeInt(p2.getMyCard());

				System.out.println("first dicision P1: " + p1.getMyCard() + " P2 :"
						+ p2.getMyCard());
				// ����
				int gamecount = 0;
				while (p1.getGarnet() > 0 && p2.getGarnet() > 0 || p1.getMyCard() == p2.getMyCard()) {
					gamecount++;
					p1.setDie(false); p2.setDie(false);
					dout.writeUTF("continue");
					dout2.writeUTF("continue");
					
					if(p1.getGarnet() > 0  && p2.getGarnet() > 0) {
						procRoom.setPanDon(2);
						p1.decGarnet(1);
						p2.decGarnet(1);
					} System.out.println("111");
					
					dout.writeInt(p1.getGarnet());
					dout2.writeInt(p2.getGarnet());	
					System.out.println("�� ����");
					dout.writeInt(p2.getGarnet());
					dout2.writeInt(p1.getGarnet());
					System.out.println("��� ����");
					dout.writeInt(procRoom.getBetSum());
					dout.writeInt(procRoom.getBetting());
					dout2.writeInt(procRoom.getBetSum());
					dout2.writeInt(procRoom.getBetting());
					System.out.println("��������");
					if (gamecount == 10) {
						procRoom.initDeck(); // 10ȸ���� ���� �����ؾ��Ѵ�.
						gamecount = 0;
						System.out.println("�� ����");
					}
					
					p1.setMyCard(procRoom.drawDeck()); // ������ ī�带 �̰�
					p2.setMyCard(procRoom.drawDeck());
					System.out.println("p1 : " + procRoom.getOwner().getMyCard() + " p2 : " + procRoom.getOther().getMyCard());
					System.out.println("pl garnet = " + p1.getGarnet() + ", p2 garnet = " + p2.getGarnet());
					dout.flush();
					dout2.flush();
								
					dout.writeInt(p2.getMyCard()); // ����� ���� ������
					dout2.writeInt(p1.getMyCard());
					 					
					String answer;//����
					do {
						if (p1.getMyTurn() == true) {//1p ��
							dout.writeUTF("Your turn");
							dout2.writeUTF("Wait turn");
							answer = bettingProc(p1, dout, din);
							System.out.println("���ù���");
							showBetting(p1, dout2, din2, answer);
						} else {
							dout2.writeUTF("Your turn");
							dout.writeUTF("Wait turn");
							answer = bettingProc(p2, dout2, din2);
							System.out.println("���ù���");
							showBetting(p2, dout, din, answer);
						}

					} while (answer.equals("betting"));
					dout.writeUTF("Betting end");
					dout2.writeUTF("Betting end");

					//���� ���� �������� 
					String p1b = String.valueOf(p1.getDie());
					String p2b = String.valueOf(p2.getDie());
					
					dout.writeUTF(p2b);
					dout2.writeUTF(p1b);
					
					
					// ���Ȯ��
					dout.writeInt(p1.getMyCard());// ��ī�� ������ �ְ�
					dout2.writeInt(p2.getMyCard());
					
					if(p1.getDie() == false && p2.getDie() == false && p1.getMyCard() == p2.getMyCard()) {
						//���º��ΰ��
						//dout.writeInt(p1.getGarnet());
						//dout2.writeInt(p2.getGarnet());	
						continue;
					}else if (p1.getDie() == true && p1.getMyCard() == 10) {//die�ߴµ� 10�ΰ��
						System.out.println("1P die �ߴµ� 10");
						p1.decGarnet(5);
						procRoom.setBetting(5);
						p2.incGarnet(procRoom.getBetSum());
						p2.setMyTurn(true);
						p1.setMyTurn(false);
					}else if(p2.getDie() == true && p2.getMyCard() == 10) {
						System.out.println("2P die �ߴµ� 10");
						p2.decGarnet(5);
						procRoom.setBetting(5);
						p1.incGarnet(procRoom.getBetSum());
						p1.setMyTurn(true);
						p2.setMyTurn(false);
					}else if (p1.getDie() == true)  {
						System.out.println("1P die");
						p2.incGarnet(procRoom.getBetSum());
						p1.setMyTurn(false);
						p2.setMyTurn(true);
						
					} else if(p2.getDie() == true) {
						System.out.println("2P die");
						p1.incGarnet(procRoom.getBetSum());
						p1.setMyTurn(true);
						p2.setMyTurn(false);						
						
					}					
					else if (p1.getMyCard() > p2.getMyCard()) {// p1 �̰��� ���  ��-ī�尡 ������� && ���̾����� ���
						p1.incGarnet(procRoom.getBetSum()); // ���ñݾ��� ��� ���� ����
						p1.setMyTurn(true);
						p2.setMyTurn(false);
					}else { //���� ��� ������ �����ϸ鼭 ���⶧���� �� �ʿ�� ����.
						p2.incGarnet(procRoom.getBetSum());
						p2.setMyTurn(true);
						p1.setMyTurn(false);
					}
					
					procRoom.initBetting();// ���� �ʱ�ȭ
										
				}
							
				procRoom.setStatus(true);
				dout.flush();
				dout2.flush();
				dout.writeUTF("Game Over");
				dout2.writeUTF("Game Over");
			}
			// �������� ����ó�� ���� ���� ���Ƿ� ���i��
			procRoom.close();
			RoomManager.deleteRoom(procRoom);
		} catch (IOException e) {
			//p1.detachRoom(procRoom);
			//p2.detachRoom(procRoom);
			//return;
			e.printStackTrace();
		}
	}
	
	private void showBetting(GameUser player, DataOutputStream send, DataInputStream recv, String op) {
		try {
			send.writeUTF(op);
			send.writeInt(procRoom.getBetting());
			send.writeInt(player.getGarnet());
			send.writeInt(procRoom.getBetSum());
			System.out.println("���õ� : " + procRoom.getBetting() + " ��� ���� ����" + player.getGarnet() + " �� ���þ� " + procRoom.getBetSum());
		}catch(IOException e) {} 		
	}

	private String bettingProc(GameUser player, DataOutputStream send, DataInputStream recv) {
		String answer = null;
		try {
			answer = recv.readUTF();
			System.out.println("get op : " + answer);
			if (answer.equals("betting")) {
				send.flush();
				send.writeInt(procRoom.getBetting());
				if (procRoom.getBetting() == 0) {// ù���� ���
					inputBetting(player, send, recv);
				} else {//���� ������ �̾�޴� ���
					int nowBetting = procRoom.getBetting();	
					nowBetting += recv.readInt();
					inputBetting(player, nowBetting);
				}
			} else if (answer.equals("call")) {
				int currentBetting = procRoom.getBetting();
				if (currentBetting > player.getGarnet()) { // ���ߴµ� ���þ׺��� ������ ������� ����ó��
					inputBetting(player, player.getGarnet());
				} else {
					inputBetting(player, currentBetting);// ��밡 �����Ѹ�ŭ ��
			
				}
				send.writeInt(procRoom.getBetting());
				send.writeInt(procRoom.getBetSum());
				send.writeInt(player.getGarnet());
			}else if (answer.equals("die")) {
				player.setDie(true);
			}
		} catch (IOException e) {}
		return answer;
	}

	void inputBetting(GameUser player, DataOutputStream send, DataInputStream recv) {
		int betting;
		try {
			betting = recv.readInt(); // ���ð� �Է¹���
			System.out.println("���ñݾ� : " + betting);
			player.decGarnet(betting); // ������ ����ŭ ���� ����
			procRoom.setBetting(betting);// ���ð� �Է�
			procRoom.changeTurn(); // ���� �ѱ�
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void inputBetting(GameUser player, int n) {
		int betting;

		betting = n;
		player.decGarnet(betting); // ������ ����ŭ ���� ����
		procRoom.setBetting(betting);// ���ð� �Է�
		procRoom.changeTurn(); // ���� �ѱ�
	}
}
