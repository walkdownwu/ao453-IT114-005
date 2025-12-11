#!/bin/bash
echo "Setting up Milestone 1 - Part 3 (Server Files)..."

# ServerThread.java
cat > src/server/ServerThread.java << 'EOF'
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import common.*;

public class ServerThread extends Thread {
    private Socket client;
    private String clientName;
    private long clientId;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isRunning = false;
    private Room currentRoom;

    public ServerThread(Socket socket) {
        this.client = socket;
        this.clientId = Thread.currentThread().getId();
    }

    public void setClientName(String name) {
        this.clientName = name;
    }

    public String getClientName() {
        return clientName;
    }

    public long getClientId() {
        return clientId;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        if (currentRoom != null) {
            currentRoom.removeClient(this);
        }
        currentRoom = room;
    }

    protected void info(String message) {
        System.out.println(String.format("Thread[%s]: %s", getId(), message));
    }

    public boolean sendConnectionStatus(long clientId, String clientName, boolean isConnect) {
        Payload p = new Payload();
        p.setPayloadType(isConnect ? PayloadType.CONNECT : PayloadType.DISCONNECT);
        p.setClientId(clientId);
        p.setClientName(clientName);
        return send(p);
    }

    public boolean sendMessage(long clientId, String message) {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.MESSAGE);
        p.setClientId(clientId);
        p.setMessage(message);
        return send(p);
    }

    public boolean sendClientId(long clientId) {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.CLIENT_ID);
        p.setClientId(clientId);
        return send(p);
    }

    public boolean sendResetUserList() {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.RESET_USER_LIST);
        return send(p);
    }

    public boolean sendClientSync(long clientId, String clientName) {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.SYNC_CLIENT);
        p.setClientId(clientId);
        p.setClientName(clientName);
        return send(p);
    }

    private boolean send(Payload payload) {
        try {
            out.writeObject(payload);
            out.flush();
            info("Sent payload: " + payload);
            return true;
        } catch (IOException e) {
            info("Error sending payload: " + e.getMessage());
            e.printStackTrace();
            cleanup();
            return false;
        }
    }

    @Override
    public void run() {
        info("Thread started");
        try {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            
            isRunning = true;
            
            Payload fromClient;
            while (isRunning && (fromClient = (Payload) in.readObject()) != null) {
                info("Received payload: " + fromClient);
                processPayload(fromClient);
            }
        } catch (Exception e) {
            if (!client.isClosed()) {
                info("Client disconnected: " + e.getMessage());
            }
        } finally {
            cleanup();
        }
    }

    private void processPayload(Payload payload) {
        if (currentRoom == null) {
            return;
        }

        switch (payload.getPayloadType()) {
            case CONNECT:
                setClientName(payload.getClientName());
                sendClientId(clientId);
                currentRoom.handleConnect(this);
                break;
            case DISCONNECT:
                currentRoom.handleDisconnect(this);
                break;
            case MESSAGE:
                currentRoom.handleMessage(this, payload.getMessage());
                break;
            case CREATE_ROOM:
                currentRoom.handleCreateRoom(this, payload.getMessage());
                break;
            case JOIN_ROOM:
                currentRoom.handleJoinRoom(this, payload.getMessage());
                break;
            default:
                break;
        }
    }

    private void cleanup() {
        isRunning = false;
        if (currentRoom != null) {
            currentRoom.handleDisconnect(this);
        }
        if (client != null && !client.isClosed()) {
            try {
                client.close();
            } catch (IOException e) {
                info("Error closing client: " + e.getMessage());
            }
        }
        info("Thread stopped");
    }
}
EOF
echo "✓ Created ServerThread.java"

# Room.java
cat > src/server/Room.java << 'EOF'
package server;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    protected List<ServerThread> clients = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected synchronized void addClient(ServerThread client) {
        if (!clients.contains(client)) {
            clients.add(client);
            client.setCurrentRoom(this);
            onClientAdded(client);
        }
    }

    protected synchronized void removeClient(ServerThread client) {
        if (clients.remove(client)) {
            onClientRemoved(client);
        }
    }

    protected void onClientAdded(ServerThread client) {
        sendConnectionStatus(client, true);
        syncExistingClients(client);
        sendRoomJoinedMessage(client);
    }

    protected void onClientRemoved(ServerThread client) {
        sendConnectionStatus(client, false);
        sendRoomLeftMessage(client);
    }

    private void syncExistingClients(ServerThread newClient) {
        for (ServerThread existingClient : clients) {
            if (existingClient != newClient) {
                newClient.sendClientSync(existingClient.getClientId(), 
                    existingClient.getClientName());
            }
        }
    }

    protected void sendConnectionStatus(ServerThread client, boolean isConnect) {
        sendToAllClients(st -> st.sendConnectionStatus(
            client.getClientId(), 
            client.getClientName(), 
            isConnect), 
            client);
    }

    protected void sendRoomJoinedMessage(ServerThread client) {
        sendMessage(null, String.format("%s joined the room", client.getClientName()));
    }

    protected void sendRoomLeftMessage(ServerThread client) {
        sendMessage(null, String.format("%s left the room", client.getClientName()));
    }

    protected void sendMessage(ServerThread sender, String message) {
        long senderId = sender == null ? -1 : sender.getClientId();
        sendToAllClients(st -> st.sendMessage(senderId, message), null);
    }

    protected void sendToAllClients(java.util.function.Function<ServerThread, Boolean> action, 
                                     ServerThread exclude) {
        for (ServerThread client : clients) {
            if (client != exclude) {
                action.apply(client);
            }
        }
    }

    public void handleConnect(ServerThread client) {
        addClient(client);
    }

    public void handleDisconnect(ServerThread client) {
        removeClient(client);
    }

    public void handleMessage(ServerThread sender, String message) {
        sendMessage(sender, message);
    }

    public void handleCreateRoom(ServerThread client, String roomName) {
        Room newRoom = Server.INSTANCE.createRoom(roomName);
        if (newRoom != null) {
            newRoom.addClient(client);
        }
    }

    public void handleJoinRoom(ServerThread client, String roomName) {
        Room targetRoom = Server.INSTANCE.getRoom(roomName);
        if (targetRoom != null) {

chmod +x setup_m1_part3.sh
./setup_m1_part3.sh
cat > setup_m1_part4.sh << 'ENDOFSCRIPT'
#!/bin/bash
echo "Setting up Milestone 1 - Part 4 (Final - CLI Client)..."

# ClientCLI.java
cat > src/client/ClientCLI.java << 'EOF'
package client;

import java.util.Scanner;
import common.*;

public class ClientCLI implements IClientEvents {
    private Client client;
    private boolean isConnected = false;
    private Scanner scanner;

    public ClientCLI() {
        client = new Client();
        client.addCallback(this);
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Rock Paper Scissors - Client");
        System.out.println("Waiting for user input...");
        
        String input;
        while (true) {
            System.out.print("> ");
            input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            processCommand(input);
        }
    }

    private void processCommand(String input) {
        String[] parts = input.split(" ", 2);
        String command = parts[0].toLowerCase();
        
        switch (command) {
            case "/name":
                if (parts.length < 2) {
                    System.out.println("Usage: /name <username>");
                } else {
                    client.setClientName(parts[1]);
                    System.out.println("Name set to: " + parts[1]);
                }
                break;
                
            case "/connect":
                if (client.getClientName().isEmpty()) {
                    System.out.println("Error: Set your name first with /name <username>");
                } else if (parts.length < 2) {
                    connect("localhost", 3000);
                } else {
                    String[] hostPort = parts[1].split(":");
                    String host = hostPort[0];
                    int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 3000;
                    connect(host, port);
                }
                break;
                
            case "/disconnect":
                client.disconnect();
                isConnected = false;
                System.out.println("Disconnected from server");
                break;
                
            case "/createroom":
                if (!isConnected) {
                    System.out.println("Error: Not connected to server");
                } else if (parts.length < 2) {
                    System.out.println("Usage: /createroom <room_name>");
                } else {
                    client.sendCreateRoom(parts[1]);
                }
                break;
                
            case "/joinroom":
                if (!isConnected) {
                    System.out.println("Error: Not connected to server");
                } else if (parts.length < 2) {
                    System.out.println("Usage: /joinroom <room_name>");
                } else {
                    client.sendJoinRoom(parts[1]);
                }
                break;
                
            case "/help":
                printHelp();
                break;
                
            case "/quit":
                System.out.println("Goodbye!");
                System.exit(0);
                break;
                
            default:
                if (!isConnected) {
                    System.out.println("Error: Not connected to server");
                } else {
                    client.sendMessage(input);
                }
                break;
        }
    }

    private void connect(String host, int port) {
        System.out.println("Attempting to connect to " + host + ":" + port + "...");
        if (client.connectToServer(host, port)) {
            isConnected = true;
            System.out.println("Connection successful!");
        } else {
            System.out.println("Connection failed!");
        }
    }

    private void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("  /name <username>           - Set your username");
        System.out.println("  /connect [host:port]       - Connect to server (default: localhost:3000)");
        System.out.println("  /disconnect                - Disconnect from server");
        System.out.println("  /createroom <name>         - Create a new room");
        System.out.println("  /joinroom <name>           - Join an existing room");
        System.out.println("  /help                      - Show this help");
        System.out.println("  /quit                      - Exit the application");
        System.out.println("  <message>                  - Send a message to current room\n");
    }

    @Override
    public void onClientId(long id) {
        System.out.println("[SYSTEM] Your client ID: " + id);
    }

    @Override
    public void onMessageReceived(long clientId, String message) {
        if (clientId == -1) {
            System.out.println("[SERVER] " + message);
        } else if (clientId == client.getClientId()) {
            System.out.println("[YOU] " + message);
        } else {
            System.out.println("[" + clientId + "] " + message);
        }
    }

    @Override
    public void onClientConnect(long clientId, String clientName) {
        System.out.println("[SYSTEM] " + clientName + " connected");
    }

    @Override
    public void onClientDisconnect(long clientId, String clientName) {
        System.out.println("[SYSTEM] " + clientName + " disconnected");
    }

    @Override
    public void onSyncClient(long clientId, String clientName) {
        System.out.println("[SYSTEM] Synced user: " + clientName + " (ID: " + clientId + ")");
    }

    @Override
    public void onResetUserList() {
        System.out.println("[SYSTEM] User list reset");
    }

    @Override
    public void onPoints(long clientId, String clientName, int points) {
    }

    @Override
    public void onPhase(String phase) {
    }

    @Override
    public void onRoundTimer(int seconds) {
    }

    @Override
    public void onAwayStatus(long clientId, String clientName, boolean isAway) {
    }

    @Override
    public void onSpectatorJoined(long clientId, String clientName) {
    }

    public static void main(String[] args) {
        ClientCLI cli = new ClientCLI();
        cli.start();
    }
}
EOF
echo "✓ Created ClientCLI.java"

echo ""
echo "========================================="
echo "ALL MILESTONE 1 FILES CREATED! (10/10)"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Run: ./create_build_scripts.sh"
echo "2. Then compile and test!"
