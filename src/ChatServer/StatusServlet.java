package ChatServer;


import ChatClient.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/status")
public class StatusServlet extends HttpServlet {
	
	private UsersDB usersDB = UsersDB.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException 
	{

		String login = req.getParameter("login");

		if (!req.getParameter("status").equals("null")) {
            Status status = Status.valueOf(req.getParameter("status"));
            usersDB.setStatus(login, status);
        }

		Status statusInDB = usersDB.getStatus(login);


		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(statusInDB);
		if (statusInDB != null) {
			OutputStream os = resp.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			os.close();
		}
	}
}
