package com.shnok.clientpackets;

public class RequestSendMessage extends ClientPacket {

    private final String _message;

    public RequestSendMessage(byte[] data) {
        super(data);

        _message = readS(0);
    }

    public String getMessage() {
        return _message;
    }
}