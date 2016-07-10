package ChatServer;

import ChatClient.Account;
import ChatClient.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersDB {

	private static final UsersDB userList = new UsersDB();

	private final Map<String, Account> map = new HashMap<>();
	{
		map.put("admin",new Account("admin", "admin"));
		map.put("user", new Account("user", "user"));
		map.put("user1",new Account("user1", "user"));
		map.put("user2",new Account("user2", "user"));
		map.put("user3",new Account("user3", "user"));
	}

	public static UsersDB getInstance() {
		return userList;
	}

  private UsersDB() {}
	
	public synchronized void addUser(String login, String pass) {
		map.put(login, new Account(login, pass));
	}
	public synchronized Account getUser(String login) {
		return map.get(login);
	}
	public synchronized String getPass(String login) {
		return map.get(login).getPass();
	}
	public synchronized Status getStatus(String login) {
		return map.get(login).getStatus();
	}
	public synchronized void setStatus(String login, Status status) {
		Account account = getUser(login);
		account.setStatus(status);
		map.put(login, account);
	}
	public synchronized String[] getAuthorizedUserArray() {
		ArrayList<String> users = new ArrayList<>();
		for (Account account : map.values()) {
			if (account.getStatus()!= Status.Offline&&account.getStatus()!= Status.Invisible) users.add(account.getLogin());
		}
		return users.toArray(new String[users.size()]);
	}

	public synchronized boolean contains(String login) {
		return map.containsKey(login);
	}


}
