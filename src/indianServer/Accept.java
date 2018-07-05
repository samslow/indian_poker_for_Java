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
	
	public Accept(Socket sock) {//클라이언트 접속을 받음
		clientSocket = sock;
		//getRoom = new GameRoom();
		//user = new GameUser();
	}
	
	public boolean isValidate(String id, String pwd) { 
		//id pwd를 어떻게 관리할까 1.db사용  2.서버가 유지 -> 서버 끝나면 다날라감 3. 파일 읽고 쓰기? 
		return true;
	}
	
	public void sendRoomInfo() {
		int count = 0;
		try {
			count = RoomManager.roomCount();
			System.out.println("방의 갯수는 " + count);
			dout.writeInt(count);
			for(int i = 0; i < count; i ++) {
				GameRoom r = RoomManager.roomList.get(i);
				dout.writeInt(r.getId());				//방id
				dout.writeUTF(r.getName());			//방 이름
				dout.writeUTF(r.getOwner().getId());//방장 이름
				dout.writeInt(r.getCurrentUser()); 	//입장가능한지
				System.out.println("전송됨 " + r.getId() + " " + r.getName() + " " + r.getOwner().getId() + " " + r.getCurrentUser());
			}
		}catch(IOException e) {}
	}

	@Override
	public void run() {
		try {
			//System.out.println("접속 대기");
			//clientSocket = listenSocket.accept();
			in = clientSocket.getInputStream();
			out = clientSocket.getOutputStream();
			din = new DataInputStream(in);
			dout = new DataOutputStream(out);
			String id =  din.readUTF();
			String pwd = din.readUTF();
			//System.out.println("받은 id : " + id + "  pwd " + pwd);
			if(!isValidate(id, pwd)) {//아이디 비번 안맞는 경우
				din.close();
				clientSocket.close();
				return;
			}
			else {
				System.out.println("Client connect IP : " + clientSocket.getInetAddress() + " PORT : " + clientSocket.getPort());
				dout.writeUTF("OK");
				//유저 등록
				user = new GameUser(id, clientSocket);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			//방 입장 or 방 생성 입력 
			while(user.getSocket() != null) {
				sendRoomInfo();
				
				String getCode = din.readUTF();
				if(getCode.equals("RoomCreate")) {
					String gameName = din.readUTF();
					user.setGameName(gameName);
					getRoom = RoomManager.createRoom(user);
					if(getRoom != null) {
						dout.writeUTF("RCS"); 
						//게임 내부처리
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
							//게임 내부 처리;;
							user.waitGame(this);
						}
						else {
							dout.writeUTF("Fail");
						}
					}
				}
			}
		} catch (IOException e) {	
			System.out.println("비정상 연결종료 " + clientSocket.getInetAddress());
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
