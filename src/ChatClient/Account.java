package ChatClient;

import java.io.Serializable;

/**
 * Created by user-pc on 09.07.2016.
 */
public class Account implements Serializable {
    private String login;
    private String pass;
    private Status status;

    public Account(String login, String pass, Status status) {
        this.login = login;
        this.pass = pass;
        this.status = status;
    }

    public Account(String login, String pass) {
        this.login = login;
        this.pass = pass;
        this.status = Status.Offline;

    }

    public Status getStatus() {
        return status;
    }


    public void setStatus(Status status) {
        this.status = status;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
