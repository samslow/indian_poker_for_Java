package indianServer;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.Serializable;
import java.util.Random;

@SuppressWarnings("serial")
public class GameRoom implements Serializable{
	private int id, usercount;
	private GameUser owner, other;
	private String roomName;
	private int deck[][] = {{1,2,3,4,5,6,7,8,9,10}, {1,2,3,4,5,6,7,8,9,10}};
	private int check[][] = new int[2][10];
	private int betting, betSum;
	private boolean status;
	
	public GameRoom() { }
	public GameRoom(int id) {
		this.id = id;
	}
	
	public GameRoom(int id, GameUser user, String name) {
		this.id = id;
		roomName = name;
		betting = 0;
		betSum = 0;
		user.joinRoom(this);
		owner = user;
		usercount = 1;
		System.out.println("방이 생성되었습니다  id " + id +" count "+ usercount + "  user = " + user.getId());
	}
	
	
	public boolean joinUser(GameUser user) {//유저가 들어온 경우
		if(usercount == 1) {
			user.joinRoom(this);
			other = user;
			usercount++;
			return true;
		}
		else 
			return false;
	}
	
	public void detachUser(GameUser user) {//유저가 나갈 경우
		user.detachRoom(this);
		
		usercount--;
		if(usercount == 1) {//방장이 나간경우	
			owner = other;
			other = null;
			return;
		}
		if(usercount == 0) {
			RoomManager.deleteRoom(this);
			return;
		}
	}
	
	public void close() {//방 폭파
		if(owner != null) {
			owner.reSetGarnet();
			detachUser(owner);
		}
		if(other != null) {
			other.reSetGarnet();
			detachUser(other);
		}
		roomName = null;
		id = 0;
		usercount = 0;
	}
	
	public void initDeck() {
		for(int i = 0; i < 2; i++) 
			for(int j = 0 ; j < 10; j++)
				check[i][j] = 0;
	}
	
	public int drawDeck() {
		Random r = new Random();
		int i, j;
		do {
		i = r.nextInt(2);
		j = r.nextInt(10);
		}while(!isAlive(i, j));
		
		return deck[i][j];	
	}
	
	public boolean isAlive(int i, int j) {
		if(check[i][j] == 0) {
			check[i][j] = 1;
			return true;
		}
		return false;
	}
	
	public void whoFirst() {
		if(owner.getMyCard() > other.getMyCard()) {
			owner.setMyTurn(true);
			other.setMyTurn(false);
		}
		else if(owner.getMyCard() < other.getMyCard()) {
			other.setMyTurn(true);
			owner.setMyTurn(false);
		}
	}
	
	public void changeTurn() {
		if(owner.getMyTurn()) {
			owner.setMyTurn(false);
			other.setMyTurn(true);
		}
		else {
			other.setMyTurn(false);
			owner.setMyTurn(true);
		}
	}
	
	public void initBetting() {
		betting = 0;
		betSum = 0;
	}
	
	public void setPanDon(int n) {
		betSum = n;
	}
	
	public void setBetting(int bet) {
		betting = bet;
		betSum += bet;
	}
	
	public int getBetting() {
		return betting;
	}
	
	public int getBetSum() {
		return betSum;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return roomName;
	}
	
	public GameUser getOwner() {
		return owner;
	}
	
	public GameUser getOther() {
		return other;
	}
	
	public int getCurrentUser() {
		return usercount;
	}
	
	public void setStatus(boolean t) {
		status = t;
	}
	
	public boolean getSatus() {
		return status;
	}
	
	@Override
	public String toString() {
		return "Room NO : " + id + ", Room name : " + roomName +", P1 " + owner + ", P2 " + other;
	}
}
