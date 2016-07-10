package ChatServer;

import java.util.HashMap;
import java.util.Map;

public class UsersDB {

	private static final UsersDB userList = new UsersDB();

	private final Map<String, String> map = new HashMap<>();
	{
		map.put("admin", "admin");
		map.put("user", "user");
		map.put("user1", "user");
		map.put("user2", "user");
		map.put("user3", "user");
	}

	public static UsersDB getInstance() {
		return userList;
	}

  private UsersDB() {}
	
	public synchronized void addUser(String login, String pass) {
		map.put(login, pass);
	}
	public synchronized String getPass(String login) {
		return map.get(login);
	}
	public synchronized String[] getUserArray() {
		return map.keySet().toArray(new String[map.keySet().size()]);
	}

	public synchronized boolean contains(String login) {
		return map.containsKey(login);
	}


}
