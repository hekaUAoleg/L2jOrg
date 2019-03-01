package org.l2j.scripts.events;

import org.l2j.scripts.npc.model.events.CustomObservationManagerInstance;
import org.l2j.commons.collections.MultiValueSet;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.holder.EventHolder;
import org.l2j.gameserver.data.xml.holder.InstantZoneHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.entity.events.EventType;
import org.l2j.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.l2j.gameserver.model.entity.events.objects.DoorObject;
import org.l2j.gameserver.model.entity.events.objects.SpawnExObject;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.s2c.ExReceiveOlympiadPacket.MatchList.ArenaInfo;
import org.l2j.gameserver.templates.InstantZone;
import org.l2j.gameserver.utils.Location;

import java.util.List;

public abstract class AbstractCustomObservableEvent extends SingleMatchEvent
{
	private static final String DOORS = "doors";
	private static final String SPAWNS = "org/l2j/scripts/spawns";

	private final int _instanceId;

	private Reflection _reflection;
	private ArenaInfo _arena = null;
	private int _arenaId = 0;
	private Location _observerCoords = null;

	public AbstractCustomObservableEvent(MultiValueSet<String> set)
	{
		super(set);
		_instanceId = set.getInteger("instance_id", getId());
	}

	protected AbstractCustomObservableEvent(int id, int status, int type, String player1, String player2)
	{
		super(id, Util.STRING_EMPTY);
		final AbstractCustomObservableEvent source = EventHolder.getInstance().getEvent(EventType.CUSTOM_PVP_EVENT, id);
		source.cloneTo(this);

		// копируем двери потому что в каждом новом рефлекте они свои
		final List<DoorObject> doorList = source.getObjects(DOORS);
		if(!doorList.isEmpty())
			for(DoorObject door : doorList)
				addObject(DOORS, new DoorObject(door.getId()));

		final List<SpawnExObject> spawnList = source.getObjects(SPAWNS);
		if(!spawnList.isEmpty())
			for(SpawnExObject spawn : spawnList)
				addObject(SPAWNS, new SpawnExObject(spawn));

		_instanceId = source.getInstanceId();
		final InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(_instanceId);
		_reflection = new Reflection();
		_reflection.init(instantZone);
		_observerCoords = instantZone.getTeleportCoords().size() > 1 ? instantZone.getTeleportCoords().get(2) : instantZone.getTeleportCoord();

		_arenaId = CustomObservationManagerInstance.registerBattle(this);
		_arena =  new ArenaInfo(_arenaId, status, type, player1, player2);
	}

	public int getInstanceId()
	{
		return _instanceId;
	}

	public ArenaInfo getArena()
	{
		return _arena;
	}

	public int getArenaId()
	{
		return _arenaId;
	}

	public Location getObserverCoords()
	{
		return _observerCoords;
	}

	public void addObserver(Player player)
	{
		//
	}

	public void removeObserver(Player player)
	{
		//
	}

	@Override
	public void stopEvent(boolean force)
	{
		super.stopEvent(force);
		CustomObservationManagerInstance.unRegisterBattle(this);
	}

	@Override
	public EventType getType()
	{
		return EventType.CUSTOM_PVP_EVENT;
	}

	@Override
	public Reflection getReflection()
	{
		return _reflection;
	}

	@Override
	public void sendPacket(IBroadcastPacket packet)
	{
		for(Creature c : _reflection.getPlayersAndObservers())
			c.sendPacket(packet);
	}

	@Override
	public void sendPackets(IBroadcastPacket... packets)
	{
		for(Creature c : _reflection.getPlayersAndObservers())
			c.sendPacket(packets);
	}

	public void sendPacketToObservers(IBroadcastPacket packet)
	{
		for(Creature c : _reflection.getObservers())
			c.sendPacket(packet);		
	}
}