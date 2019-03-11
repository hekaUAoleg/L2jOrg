/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.gameserver.mobius.gameserver.enums.Race;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.mobius.gameserver.model.base.ClassType;
import org.l2j.gameserver.mobius.gameserver.model.base.PlayerClass;

public final class L2VillageMasterFighterInstance extends L2VillageMasterInstance {
    /**
     * Creates a village master.
     *
     * @param template the village master NPC template
     */
    public L2VillageMasterFighterInstance(L2NpcTemplate template) {
        super(template);
    }

    @Override
    protected final boolean checkVillageMasterRace(PlayerClass pclass) {
        if (pclass == null) {
            return false;
        }

        return pclass.isOfRace(Race.HUMAN) || pclass.isOfRace(Race.ELF);
    }

    @Override
    protected final boolean checkVillageMasterTeachType(PlayerClass pclass) {
        if (pclass == null) {
            return false;
        }

        return pclass.isOfType(ClassType.Fighter);
    }
}