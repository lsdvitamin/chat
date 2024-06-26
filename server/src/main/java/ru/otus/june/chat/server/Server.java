package ru.otus.june.chat.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("В чат зашел: " + clientHandler.getUsername());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: " + clientHandler.getUsername());
    }


    public synchronized void privateMessage(String username, String message, ClientHandler sender) {
        String[] privatMessages = message.split(" ");
        String targetUser = privatMessages[1];
        String privatMessage = privatMessages[2];
        ClientHandler user = searchByUser(targetUser);
        if (Objects.isNull(user)) {
            sender.sendMessage("Пользователь " + targetUser + " не найден");
        } else {
            user.sendMessage(privatMessage);
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public ClientHandler searchByUser(String user) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(user)) {
                return c;
            }
        }
        return null;
    }

}
