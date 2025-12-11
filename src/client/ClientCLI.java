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
        System.out.println("  /connect [host:port]       - Connect to server");
        System.out.println("  /disconnect                - Disconnect from server");
        System.out.println("  /createroom <name>         - Create a new room");
        System.out.println("  /joinroom <name>           - Join an existing room");
        System.out.println("  /help                      - Show this help");
        System.out.println("  /quit                      - Exit\n");
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
    public void onResetUserList() {}
    @Override
    public void onPoints(long clientId, String clientName, int points) {}
    @Override
    public void onPhase(String phase) {}
    @Override
    public void onRoundTimer(int seconds) {}
    @Override
    public void onAwayStatus(long clientId, String clientName, boolean isAway) {}
    @Override
    public void onSpectatorJoined(long clientId, String clientName) {}

    public static void main(String[] args) {
        ClientCLI cli = new ClientCLI();
        cli.start();
    }
}
