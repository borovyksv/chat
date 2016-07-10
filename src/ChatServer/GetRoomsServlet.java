package ChatServer;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/rooms")
public class GetRoomsServlet extends HttpServlet {
	
	private RoomList roomList = RoomList.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException 
	{
		String room = req.getParameter("room");
		if (!room.equals("null")) roomList.add(room);
		String json = roomList.toJSON();
		if (json != null) {
			OutputStream os = resp.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			os.close();
		}
	}
}
