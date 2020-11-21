package org.l2j.gameserver.network.clientpackets.castle;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ExMercenarySiegeHUDInfo;

public class ExCastleWarSiegeInfo extends ClientPacket {
    private int castleid;
    @Override
    protected void readImpl() throws Exception {
        // dummy byte
        castleid = readInt();
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExMercenarySiegeHUDInfo(castleid));
    }
}