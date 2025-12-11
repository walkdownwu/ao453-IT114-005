#!/bin/bash
echo "Setting up Milestone 1 - All Files..."

# Constants.java
cat > src/common/Constants.java << 'EOF'
package common;

public class Constants {
    public static final int ROUND_TIMER = 30;
    public static final String LOBBY = "Lobby";
}
EOF
echo "✓ Created Constants.java"

# PayloadType.java
cat > src/common/PayloadType.java << 'EOF'
package common;

public enum PayloadType {
    CONNECT,
    DISCONNECT,
    MESSAGE,
    CLIENT_ID,
    SYNC_CLIENT,
    JOIN_ROOM,
    CREATE_ROOM,
    RESET_USER_LIST
}
EOF
echo "✓ Created PayloadType.java"

# Payload.java
cat > src/common/Payload.java << 'EOF'
package common;

import java.io.Serializable;

public class Payload implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private PayloadType payloadType;
    private String message;
    private long clientId;
    private String clientName;

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return String.format("Payload[type=%s, clientId=%d, clientName=%s, message=%s]",
                payloadType, clientId, clientName, message);
    }
}
EOF
echo "✓ Created Payload.java"

# IClientEvents.java
cat > src/client/IClientEvents.java << 'EOF'
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
EOF
echo "✓ Created IClientEvents.java"

echo ""
echo "========================================="
echo "Milestone 1 - Part 1 Complete!"
echo "========================================="
echo "Files created: 4/10"
echo ""
echo "Run this script again? NO"
echo "Next: Run ./setup_m1_part2.sh"
