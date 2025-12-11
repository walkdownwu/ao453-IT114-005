package client;

public interface IClientEvents {
    void onClientId(long id);
    void onMessageReceived(long clientId, String message);
    void onClientConnect(long clientId, String clientName);
    void onClientDisconnect(long clientId, String clientName);
    void onSyncClient(long clientId, String clientName);
    void onResetUserList();
    void onPoints(long clientId, String clientName, int points);
    void onPhase(String phase);
    void onRoundTimer(int seconds);
    void onAwayStatus(long clientId, String clientName, boolean isAway);
    void onSpectatorJoined(long clientId, String clientName);
}
