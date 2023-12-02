package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;

public class PingPacket extends ServerPacket {
    public PingPacket() {
        super((byte) 0x00);
        setData(new byte[]{0x00, 0x02});
    }
}