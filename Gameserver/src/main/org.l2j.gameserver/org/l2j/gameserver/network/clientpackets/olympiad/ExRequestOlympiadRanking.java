/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRankingInfo;

/**
 * @author JoeAlisson
 */
public class ExRequestOlympiadRanking extends ClientPacket {

    private byte type;
    private byte scope;
    private boolean currentSeason;
    private int classId;
    private int worldId;

    @Override
    protected void readImpl() throws Exception {
        type = readByte(); // 0 - Server; 1 -> Class
        scope = readByte(); // 0 - top;  1 - My Rank
        currentSeason = readBoolean();
        classId = readInt();
        worldId = readInt(); // 0 - all
    }

    @Override
    protected void runImpl()  {
        Olympiad.getInstance().showRanking(client.getPlayer(), type, scope, currentSeason, classId, worldId);
    }
}
