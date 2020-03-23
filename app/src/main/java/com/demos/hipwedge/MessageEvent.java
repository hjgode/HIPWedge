package com.demos.hipwedge;

public class MessageEvent {
    public static enum MessageDirection{
        RECEIVE,
        TRANSMIT,
        STATUS
    }
    public static enum MessageStatus{
        CONNECTED,
        DISCONNECTED
    }
    public MessageDirection messageDirection;
    public String message;
    public MessageStatus messageStatus;

    public MessageEvent(MessageDirection direction, String msg) {
        messageDirection=direction;
        message=msg;
    }
    public MessageEvent(MessageDirection direction, MessageStatus status){
        messageStatus=status;
    }
}
