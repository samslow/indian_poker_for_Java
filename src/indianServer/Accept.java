package indianServer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Accept implements Runnable{
//	private ServerSocket listenSocket;
	private OutputStream out;
	private DataOutputStream dout;
//	private ObjectOutputStream oout;
	private InputStream in;
	private DataInputStream din;
	private Socket clientSocket;
	private GameUser user = null;
	private GameRoom getRoom = null;
	
	public Accept(Socket sock) {//Ŭ���̾�Ʈ ������ ����
		clientSocket = sock;
		//getRoom = new GameRoom();
		//user = new GameUser();
	}
	
	public boolean isValidate(String id, String pwd) { 
		//id pwd�� ��� �����ұ� 1.db���  2.������ ���� -> ���� ������ �ٳ��� 3. ���� �а� ����? 
		return true;
	}
	
	public void sendRoomInfo() {
		int count = 0;
		try {
			count = RoomManager.roomCount();
			System.out.println("���� ������ " + count);
			dout.writeInt(count);
			for(int i = 0; i < count; i ++) {
				GameRoom r = RoomManager.roomList.get(i);
				dout.writeInt(r.getId());				//��id
				dout.writeUTF(r.getName());			//�� �̸�
				dout.writeUTF(r.getOwner().getId());//���� �̸�
				dout.writeInt(r.getCurrentUser()); 	//���尡������
				System.out.println("���۵� " + r.getId() + " " + r.getName() + " " + r.getOwner().getId() + " " + r.getCurrentUser());
			}
		}catch(IOException e) {}
	}

	@Override
	public void run() {
		try {
			//System.out.println("���� ���");
			//clientSocket = listenSocket.accept();
			in = clientSocket.getInputStream();
			out = clientSocket.getOutputStream();
			din = new DataInputStream(in);
			dout = new DataOutputStream(out);
			String id =  din.readUTF();
			String pwd = din.readUTF();
			//System.out.println("���� id : " + id + "  pwd " + pwd);
			if(!isValidate(id, pwd)) {//���̵� ��� �ȸ´� ���
				din.close();
				clientSocket.close();
				return;
			}
			else {
				System.out.println("Client connect IP : " + clientSocket.getInetAddress() + " PORT : " + clientSocket.getPort());
				dout.writeUTF("OK");
				//���� ���
				user = new GameUser(id, clientSocket);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			//�� ���� or �� ���� �Է� 
			while(user.getSocket() != null) {
				sendRoomInfo();
				
				String getCode = din.readUTF();
				if(getCode.equals("RoomCreate")) {
					String gameName = din.readUTF();
					user.setGameName(gameName);
					getRoom = RoomManager.createRoom(user);
					if(getRoom != null) {
						dout.writeUTF("RCS"); 
						//���� ����ó��
						user.waitGame(this);
					}
					else dout.writeUTF("Fail");
				}
				else if(getCode.equals("RoomJoin")) {
					int roomid = din.readInt();
					getRoom = RoomManager.findRoom(roomid);
					if(getRoom != null) {
						if(getRoom.joinUser(user)) {
							dout.writeUTF("RJS");
							//���� ���� ó��;;
							user.waitGame(this);
						}
						else {
							dout.writeUTF("Fail");
						}
					}
				}
			}
		} catch (IOException e) {	
			System.out.println("������ �������� " + clientSocket.getInetAddress());
			return;
		}
		
		getRoom.detachUser(user);
	}
	public GameRoom getMyRoom() {
		return getRoom;
	}
	public void sendMsg(String str) {
		try{
			dout.writeUTF(str);
		}catch(IOException e) {return ;}
	}
	
	public String recvMsg() {
		String ret = null;
		try {
			ret = din.readUTF();
		}catch(IOException e) {e.printStackTrace();}
		return ret;
	}
}
