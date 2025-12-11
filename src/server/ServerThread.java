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
