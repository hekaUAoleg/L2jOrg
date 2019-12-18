package handlers.effecthandlers;

import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ReflectSkill extends AbstractEffect {
	private final Stat stat;
	private final double amount;
	
	public ReflectSkill(StatsSet params) {
		stat = params.getEnum("type", BasicProperty.class, BasicProperty.PHYSICAL) == BasicProperty.PHYSICAL ? Stat.REFLECT_SKILL_PHYSIC : Stat.REFLECT_SKILL_MAGIC;
		amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		effected.getStats().mergeAdd(stat, amount);
	}
}
