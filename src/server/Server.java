package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import common.Constants;

public class Server {
    public static Server INSTANCE;
    
    private int port = 3000;
    private Map<String, Room> rooms = new HashMap<>();
    
    private void start(int port) {
        this.port = port;
        System.out.println("Server starting on port " + port);
        
        Room lobby = new Room(Constants.LOBBY);
        rooms.put(Constants.LOBBY, lobby);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, listening on port " + port);
            
            while (true) {
                System.out.println("Waiting for client connection...");
                Socket client = serverSocket.accept();
                System.out.println("Client connected from " + client.getInetAddress());
                
                ServerThread thread = new ServerThread(client);
                thread.start();
                
                lobby.addClient(thread);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Room createRoom(String roomName) {
        if (rooms.containsKey(roomName)) {
            System.out.println("Room " + roomName + " already exists");
            return null;
        }
        
        Room room = new Room(roomName);
        rooms.put(roomName, room);
        System.out.println("Created room: " + roomName);
        return room;
    }

    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public static void main(String[] args) {
        INSTANCE = new Server();
        
        int port = 3000;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default 3000");
            }
        }
        
        INSTANCE.start(port);
    }
}
