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
