package ChatServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.Set;

public class RoomList {

	private static final RoomList roomList = new RoomList();

	private final Set<String> list = new HashSet<>();

	public static RoomList getInstance() {
		return roomList;
	}

  private RoomList() {}
	
	public synchronized void add(String room) {
		list.add(room);
	}
	
	public synchronized String toJSON() {

		if (list.size() > 0) {
			Gson gson = new GsonBuilder().create();
			return gson.toJson(list.toArray(new String[list.size()]));
		} else
			return null;
	}
}
