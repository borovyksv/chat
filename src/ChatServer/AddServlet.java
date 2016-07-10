package ChatServer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/add")
public class AddServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();
	private RoomList roomList = RoomList.getInstance();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		InputStream is = req.getInputStream();
		byte[] buf = new byte[req.getContentLength()];
		is.read(buf);

		Message msg = Message.fromJSON(new String(buf));
		if (msg != null){
			msgList.add(msg);
			if (msg.getRoom()!=null)roomList.add(msg.getRoom());
		}
		else
			resp.setStatus(400); // Bad request
	}
}
