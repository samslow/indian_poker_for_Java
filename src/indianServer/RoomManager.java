package indianServer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
                                                                                                                                                                        
public class RoomManager {
	public static List<GameRoom> roomList = new ArrayList<GameRoom>();
	private static AtomicInteger atom = new AtomicInteger(); //임계영역 기능을 해준다고 보면됨
	
	public static GameRoom createRoom() {//test용
		int roomId = atom.incrementAndGet();
		GameRoom room = new GameRoom();
		roomList.add(room);
		return room;
	}
	
	public static GameRoom createRoom(GameUser owner) {
		int roomId = atom.incrementAndGet();
		
		GameRoom room = new GameRoom(roomId, owner, owner.getGameName());
		roomList.add(room);
		
		return room;
	}
	
	public static void deleteRoom(GameRoom room) {
		if(room != null && roomList.contains(room) == true)
			roomList.remove(room);
		System.out.println("방이 제거되었습니다. 남은방의 수 :" +  roomList.size());
		
	}
	
	public static int roomCount() {	
		return roomList.size();
	}
	
	
	public static GameRoom findRoom(int roomid) {
		GameRoom index = null;
		
		for(int i = 0; i < roomList.size(); i++) {
			index = roomList.get(i);
			if(index.getId() == roomid) return index;
		}
		return index;
	}
}
