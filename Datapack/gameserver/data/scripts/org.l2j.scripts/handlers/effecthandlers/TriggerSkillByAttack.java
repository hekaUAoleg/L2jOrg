package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageDealt;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger Skill By Attack effect implementation.
 * @author Zealar
 */
public final class TriggerSkillByAttack extends AbstractEffect {
	private final int minAttackerLevel;
	private final int maxAttackerLevel;
	private final int minDamage;
	private final int chance;
	private final SkillHolder skill;
	private final TargetType targetType;
	private final InstanceType instanceType;
	private int allowWeapons;
	private final Boolean isCritical;
	private final boolean allowNormalAttack;
	private final boolean allowSkillAttack;
	private final boolean allowReflect;
	
	/**
	 * @param params
	 */
	
	public TriggerSkillByAttack(StatsSet params) {
		minAttackerLevel = params.getInt("minAttackerLevel", 1);
		maxAttackerLevel = params.getInt("maxAttackerLevel", 127);
		minDamage = params.getInt("minDamage", 1);
		chance = params.getInt("chance", 100);
		skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1));
		targetType = params.getEnum("targetType", TargetType.class, TargetType.SELF);
		instanceType = params.getEnum("attackerType", InstanceType.class, InstanceType.Creature);
		isCritical = params.getObject("isCritical", Boolean.class);
		allowNormalAttack = params.getBoolean("allowNormalAttack", true);
		allowSkillAttack = params.getBoolean("allowSkillAttack", false);
		allowReflect = params.getBoolean("allowReflect", false);
		
		if (params.getString("allowWeapons", "ALL").equalsIgnoreCase("ALL")) {
			allowWeapons = 0;
		} else {
			for (String s : params.getString("allowWeapons").split(",")) {
				allowWeapons |= WeaponType.valueOf(s).mask();
			}
		}
	}
	
	private void onAttackEvent(OnCreatureDamageDealt event) {
		if (event.isDamageOverTime() || (chance == 0) || ((skill.getSkillId() == 0) || (skill.getLevel() == 0)) || (!allowNormalAttack && !allowSkillAttack)) {
			return;
		}
		
		// Check if there is dependancy on critical.
		if (nonNull(isCritical) && (isCritical != event.isCritical())) {
			return;
		}
		
		// When no skill attacks are allowed.
		if (!allowSkillAttack && nonNull(event.getSkill())) {
			return;
		}
		
		// When no normal attacks are allowed.
		if (!allowNormalAttack && nonNull(event.getSkill())) {
			return;
		}
		
		if (!allowReflect && event.isReflect()) {
			return;
		}
		
		if (event.getAttacker() == event.getTarget()) {
			return;
		}
		
		if ((event.getAttacker().getLevel() < minAttackerLevel) || (event.getAttacker().getLevel() > maxAttackerLevel)) {
			return;
		}
		
		if ((event.getDamage() < minDamage) || (Rnd.get(100) > chance) || !event.getAttacker().getInstanceType().isType(instanceType)) {
			return;
		}
		
		if (allowWeapons > 0) {
			if ((event.getAttacker().getActiveWeaponItem() == null) || ((event.getAttacker().getActiveWeaponItem().getItemType().mask() & allowWeapons) == 0)) {
				return;
			}
		}
		
		final Skill triggerSkill = skill.getSkill();
		WorldObject target = null;
		try {
			target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getAttacker(), event.getTarget(), triggerSkill, false, false, false);
		} catch (Exception e) {
			LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if (isCreature(target))
		{
			final BuffInfo info = ((Creature) target).getEffectList().getBuffInfoBySkillId(triggerSkill.getId());
			if ((info == null) || (info.getSkill().getLevel() < triggerSkill.getLevel()))
			{
				SkillCaster.triggerCast(event.getAttacker(), (Creature) target, triggerSkill);
			}
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_DEALT, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_DAMAGE_DEALT, (Consumer<OnCreatureDamageDealt>) this::onAttackEvent, this));
	}
}
