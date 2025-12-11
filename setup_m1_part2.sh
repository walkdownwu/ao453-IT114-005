#!/bin/bash
echo "Setting up Milestone 1 - Part 2..."

# Client.java - THE BIG ONE
cat > src/client/Client.java << 'EOF'
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import common.*;

public class Client {
    private Socket server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = false;
    private Thread inputThread;
    private String clientName = "";
    private long clientId = -1;
    private IClientEvents events;

    public void setClientName(String name) {
        this.clientName = name;
    }

    public String getClientName() {
        return clientName;
    }

    public long getClientId() {
        return clientId;
    }

    public void addCallback(IClientEvents events) {
        this.events = events;
    }

    public boolean connectToServer(String host, int port) {
        try {
            server = new Socket(host, port);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
            
            System.out.println("Connected to server");
            
            isRunning = true;
            startListening();
            
            sendConnect();
            
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
        return false;
    }

    private void startListening() {
        inputThread = new Thread(() -> {
            try {
                Payload fromServer;
                while (isRunning && (fromServer = (Payload) in.readObject()) != null) {
                    processPayload(fromServer);
                }
            } catch (Exception e) {
                if (isRunning) {
                    System.err.println("Connection lost: " + e.getMessage());
                }
            } finally {
                disconnect();
            }
        });
        inputThread.start();
    }

    private void processPayload(Payload payload) {
        System.out.println("Received: " + payload);
        
        if (events == null) return;
        
        switch (payload.getPayloadType()) {
            case CLIENT_ID:
                clientId = payload.getClientId();
                events.onClientId(clientId);
                break;
            case CONNECT:
                events.onClientConnect(payload.getClientId(), payload.getClientName());
                break;
            case DISCONNECT:
                events.onClientDisconnect(payload.getClientId(), payload.getClientName());
                break;
            case MESSAGE:
                events.onMessageReceived(payload.getClientId(), payload.getMessage());
                break;
            case SYNC_CLIENT:
                events.onSyncClient(payload.getClientId(), payload.getClientName());
                break;
            case RESET_USER_LIST:
                events.onResetUserList();
                break;
            default:
                break;
        }
    }

    public void sendConnect() {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.CONNECT);
        p.setClientName(clientName);
        send(p);
    }

    public void sendMessage(String message) {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.MESSAGE);
        p.setMessage(message);
        p.setClientId(clientId);
        send(p);
    }

    public void sendCreateRoom(String roomName) {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.CREATE_ROOM);
        p.setMessage(roomName);
        send(p);
    }

    public void sendJoinRoom(String roomName) {
        Payload p = new Payload();
        p.setPayloadType(PayloadType.JOIN_ROOM);
        p.setMessage(roomName);
        send(p);
    }

    private void send(Payload payload) {
        try {
            out.writeObject(payload);
            out.flush();
            System.out.println("Sent: " + payload);
        } catch (IOException e) {
            System.err.println("Error sending: " + e.getMessage());
        }
    }

    public void disconnect() {
        isRunning = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (server != null && !server.isClosed()) server.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
        System.out.println("Disconnected from server");
    }
}
EOF
echo "✓ Created Client.java"

echo ""
echo "Files created: 5/10"
echo "Next: Run ./setup_m1_part3.sh"
