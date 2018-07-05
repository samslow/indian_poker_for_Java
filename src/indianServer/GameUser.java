package indianServer;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.Socket;

public class GameUser {
	private String myId;
	private GameRoom myRoom;
	private Socket mySocket;
	private String myGameName;
	private int myGarnet, myCard;
	private boolean myTurn, die;
	private int port;
	//public GameUser() {}
	public GameUser(String id, Socket sock) {
		myId = id;
		mySocket = sock;
		myRoom = null;
		myGameName = null;
		myGarnet = 20;
		myTurn = false;
		die = false;
	}
	
	public void joinRoom(GameRoom room) {
		myRoom = room;
		myGameName = room.getName();
	}
	
	public void detachRoom(GameRoom room) {
		myRoom = null;
		myGameName = null;
	}
	
	public void waitGame(Accept acc) {
		int delayTime = 4000;
		try {
			Robot r = new Robot();
			while(myRoom.getCurrentUser() < 2) {
				r.delay(delayTime);
				acc.sendMsg("wait");
			}
			acc.sendMsg("Game Start");
			GameProc go = new GameProc(this);
		} catch (AWTException e1) {	
			return;
		} catch(NullPointerException e) {
		}
		
	}
	
	public GameRoom getRoom() {
		return myRoom;
	}
	
	public String getId() {
		return myId;
	}
	
	public Socket getSocket() {
		return mySocket;
	}
	
	public String getGameName() {
		return myGameName;
	}
	
	public void setGameName(String str) {
		myGameName = str;
	}
	
	public int getGarnet() {
		return myGarnet;
	}
	
	public void incGarnet(int n) {
		myGarnet += n;
	}
	
	public void decGarnet(int n) {
		myGarnet -= n;
	}
	
	public void reSetGarnet() {
		myGarnet = 20;
	}
	
	public void setMyTurn(boolean turn) {
		myTurn = turn;
	}
	
	public boolean getMyTurn() {
		return myTurn;
	}
	
	public void setMyCard(int x) {
		myCard = x;
	}
	
	public int getMyCard() {
		return myCard;
	}
	
	public void setDie(boolean d) {
		die = d;
	}
	
	public boolean getDie() {
		return die;
	}
	
	@Override
	public String toString() {
		return "ID : " + myId ;
	}
}
