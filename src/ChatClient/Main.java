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
                                if ((account.getRoom()==null&&m.getRoom()==null)||m.getRoom()==null||m.getRoom().equals(account.getRoom())) {
                                    System.out.println(m);
                                }
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

class RoomsThread extends Thread {
    private String room;

    public RoomsThread(String room) {
        this.room = room;
        setDaemon(true);
    }

    @Override
    public void run() {
        try (InputStream is = (new URL("http://localhost:8080/rooms?room=" + room).openConnection().getInputStream())) {
            String[] rooms = (String[]) JsonParser.parseFromJson(is, String[].class);
            if (rooms == null) {
                System.out.println("Нет комнат");
            } else {
                if (room.equals("null")) {
                    System.out.println("Доступные комнаты: ");
                    System.out.println();
                    for (String room : rooms) {
                        System.out.println(room);
                    }
                } else {
                    System.out.println("Добро пожаловать в комнату: " + room);
                }
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
                if (text.isEmpty()){
                    account.setStatus(Status.Offline);
                    break;
                }

                Message m = new Message();
                m.setText(text);
                m.setFrom(login);
                //если сообщение пользователя имеет формат: "!to userName message...",
                // сообщение идет лично пользователю "userName";
                if (text.startsWith("!to ")) m.setTo(text.split(" ")[1]);
                if (text.startsWith("!users")) new GetUsersThread().start();
                if (text.startsWith("!room !")) {
                    String room = text.split(" ")[1];
                    room = room.substring(1, room.length());
                    if (room.equals("main")) {
                        room = null;
                    } else {
                        new RoomsThread(room).start();
                    }
                    account.setRoom(room);
                }
                if (text.startsWith("!rooms")) new RoomsThread("null").start();
                if (text.startsWith("!status")) {
                    String status = "null";
                    for (Status st : Status.values()) {
                        if (text.startsWith("!status !" + st.toString())) {
                            status = st.toString();
                        }
                    }
                    new StatusThread(login, status).start();
                }
                if (text.startsWith("!help")) {
                    System.out.println("Доступные команды: ");
                    System.out.println();
                    System.out.println("!to <username> <message> - отправить приватное сообщение c текстом message пользователю username");
                    System.out.println("!users - список пользователей");
                    System.out.println("!room !<roomname> - перейти в комнату roomname");
                    System.out.println("!rooms - список комнат");
                    System.out.println("!status - Ваш текущий статус");
                    System.out.println("!status !Offline - сменить статус на офлайн или любой из доступных: Online, Away, DoNotDisturb, Invisible, Offline");
                    System.out.println();
                    System.out.println("Перед каждой из команд не забываем ставить восклицательный знак (!)");
                    System.out.println();
                }


                try {
                    if (account.getRoom() != null) m.setRoom(account.getRoom());
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

class StatusThread extends Thread {
    private String login;
    private String st;

    StatusThread(String login, String st) {
        this.login = login;
        this.st = st;
        setDaemon(true);
    }

    @Override
    public void run() {
        try (InputStream is = (new URL("http://localhost:8080/status?login=" + login + "&status=" + st).openConnection().getInputStream())) {
            Status status = (Status) JsonParser.parseFromJson(is, Status.class);
            System.out.println(login + ", Ваш статус: " + status);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

