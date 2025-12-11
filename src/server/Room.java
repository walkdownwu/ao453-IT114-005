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
            targetRoom.addClient(client);
        }
    }
}
