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
package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.settings.CharacterSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author xban1x
 */
public final class CommandChannelTimer implements Runnable {
    private final Attackable _attackable;

    public CommandChannelTimer(Attackable attackable) {
        _attackable = attackable;
    }

    @Override
    public void run() {
        if (_attackable == null) {
            return;
        }

        if ((System.currentTimeMillis() - _attackable.getCommandChannelLastAttack()) > getSettings(CharacterSettings.class).raidLootPrivilegeTime()) {
            _attackable.setCommandChannelTimer(null);
            _attackable.setFirstCommandChannelAttacked(null);
            _attackable.setCommandChannelLastAttack(0);
        } else {
            ThreadPool.schedule(this, 10000); // 10sec
        }
    }

}
