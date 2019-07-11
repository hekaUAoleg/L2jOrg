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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.L2Spawn;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Tower;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for Control Tower instance.
 */
public class ControlTower extends Tower {
    private volatile Set<L2Spawn> _guards;

    public ControlTower(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2ControlTowerInstance);
    }

    @Override
    public boolean doDie(Creature killer) {
        if (getCastle().getSiege().isInProgress()) {
            getCastle().getSiege().killedCT(this);

            if ((_guards != null) && !_guards.isEmpty()) {
                for (L2Spawn spawn : _guards) {
                    if (spawn == null) {
                        continue;
                    }
                    try {
                        spawn.stopRespawn();
                        // spawn.getLastSpawn().doDie(spawn.getLastSpawn());
                    } catch (Exception e) {
                        LOGGER.warn("Error at ControlTower", e);
                    }
                }
                _guards.clear();
            }
        }
        return super.doDie(killer);
    }

    public void registerGuard(L2Spawn guard) {
        getGuards().add(guard);
    }

    private Set<L2Spawn> getGuards() {
        if (_guards == null) {
            synchronized (this) {
                if (_guards == null) {
                    _guards = ConcurrentHashMap.newKeySet();
                }
            }
        }
        return _guards;
    }
}