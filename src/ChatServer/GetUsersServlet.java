package ChatServer;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/users")
public class GetUsersServlet extends HttpServlet {
	
	private UsersDB usersDB = UsersDB.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException 
	{
		String[] users = usersDB.getUserArray();

		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(users);
		if (users != null) {
			OutputStream os = resp.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			os.close();
		}
	}
}
