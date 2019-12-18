package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Dam Over Time effect implementation.
 */
public final class DamOverTime extends AbstractEffect {
	private final boolean canKill;
	private final double power;
	
	public DamOverTime(StatsSet params) {
		canKill = params.getBoolean("canKill", false);
		power = params.getDouble("power");
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (!skill.isToggle() && skill.isMagic()) {
			// TODO: M.Crit can occur even if this skill is resisted. Only then m.crit damage is applied and not debuff
			final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
			if (mcrit) {
				double damage = power * 10; // Tests show that 10 times HP DOT is taken during magic critical.
				
				if (!canKill && (damage >= (effected.getCurrentHp() - 1))) {
					damage = effected.getCurrentHp() - 1;
				}
				
				effected.reduceCurrentHp(damage, effector, skill, true, false, true, false);
			}
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return false;
		}
		
		double damage = power * getTicksMultiplier();
		if (damage >= effected.getCurrentHp() - 1) {
			if (skill.isToggle()) {
				effected.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!canKill) {
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (effected.getCurrentHp() <= 1) {
					return false;
				}
				damage = effected.getCurrentHp() - 1;
			}
		}
		
		effector.doAttack(damage, effected, skill, true, false, false, false);
		return skill.isToggle();
	}
}
