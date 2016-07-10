package ChatServer;

import ChatClient.Account;
import ChatClient.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/authorization")
public class LoginServlet extends HttpServlet {

    UsersDB users = UsersDB.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String login = req.getParameter("login");
        Account account = null;
        //если login есть в ДБ, возвращаем аккаунт, иначе null
        if (users.contains(login)) {
            account = users.getUser(login);
            account.setStatus(Status.Online);

        }

        OutputStream os = resp.getOutputStream();
        Gson gson = new GsonBuilder().create();
        String response =  gson.toJson(account);
        os.write(response.getBytes());
        os.flush();
        os.close();
    }
}
