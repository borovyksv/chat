package ChatClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class GetThread extends Thread {
    private int n;
    private Account account;

    GetThread(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                URL url = new URL("http://localhost:8080/get?from=" + n);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                InputStream is = http.getInputStream();
                try {
                    int sz = is.available();
                    if (sz > 0) {
                        byte[] buf = new byte[is.available()];
                        is.read(buf);

                        Gson gson = new GsonBuilder().create();
                        Message[] list = gson.fromJson(new String(buf), Message[].class);

                        for (Message m : list) {
                            if (m.getTo() == null
                                    || m.getTo().equals(account.getLogin())
                                    || m.getFrom().equals(account.getLogin())) {
                                System.out.println(m);
                            }
                            n++;
                        }
                    }
                } finally {
                    is.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}

class GetUsersThread extends Thread {
    public GetUsersThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
        try (InputStream is = (new URL("http://localhost:8080/users").openConnection().getInputStream())) {
            String[] users = (String[]) JsonParser.parseFromJson(is, String[].class);
            System.out.println("В чате присутствуют: ");
            System.out.println();
            for (String user : users) {
                System.out.println(user);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String login;
            Account account = null;
            do {
                System.out.println("Введите логин: ");
                login = scanner.nextLine();
            } while ((account = authorization(login)) == null);
            //создаем Трэд только после прохождения авторизации
            GetThread th = new GetThread(account);
            th.setDaemon(true);
            th.start();

            while (true) {
                String text = scanner.nextLine();
                if (text.isEmpty())
                    break;

                Message m = new Message();
                m.setText(text);
                m.setFrom(login);
                //если сообщение пользователя имеет формат: "!to userName message...",
                // сообщение идет лично пользователю "userName";
                if (text.startsWith("!to ")) m.setTo(text.split(" ")[1]);
                if (text.startsWith("!users")) new GetUsersThread().start();

                try {
                    int res = m.send("http://localhost:8080/add");
                    if (res != 200) {
                        System.out.println("HTTP error: " + res);
                        return;
                    }
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    return;
                }
            }
        } finally {
            scanner.close();
        }
    }

    //метод возвращает @Account, если пользователь правильно введет логин и пароль, захаркожен в UsersDB
    private static Account authorization(String login) {
        Scanner scanner = new Scanner(System.in);

        try (InputStream is = (new URL("http://localhost:8080/authorization?login=" + login)).openConnection().getInputStream()) {
            Account acc = (Account) JsonParser.parseFromJson(is, Account.class);
            //нашли пользователя - вводим пароль, иначе повторяем авторизацию
            if (acc != null) System.out.println("Пользователь найден, введите пароль:");
            else {
                System.out.println("Такой пользователь не зарегистрирован");
                return null;
            }
            do {
                String pass = scanner.nextLine();
                if (pass.equals(acc.getPass())) {
                    System.out.println("Добро пожаловать в чат, " + acc.getLogin() + ". Ваш статус : " + acc.getStatus());
                    return acc;
                } else {
                    System.out.println("Пароль неверный, повторите ввод: ");
                }
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
