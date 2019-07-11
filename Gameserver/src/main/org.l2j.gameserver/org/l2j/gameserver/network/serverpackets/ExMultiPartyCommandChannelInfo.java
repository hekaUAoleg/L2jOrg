package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Objects;

/**
 * @author chris_00
 */
public class ExMultiPartyCommandChannelInfo extends ServerPacket {
    private final CommandChannel _channel;

    public ExMultiPartyCommandChannelInfo(CommandChannel channel) {
        Objects.requireNonNull(channel);
        _channel = channel;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_MULTI_PARTY_COMMAND_CHANNEL_INFO);

        writeString(_channel.getLeader().getName());
        writeInt(0x00); // Channel loot 0 or 1
        writeInt(_channel.getMemberCount());

        writeInt(_channel.getPartys().size());
        for (L2Party p : _channel.getPartys()) {
            writeString(p.getLeader().getName());
            writeInt(p.getLeaderObjectId());
            writeInt(p.getMemberCount());
        }
    }

}
