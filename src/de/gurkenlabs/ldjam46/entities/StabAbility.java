package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.AbilityInfo;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICombatEntity;

@AbilityInfo(name = "StabAbility", cooldown = 5000, range = 0, impact = 30, impactAngle = 90, value = 10, duration = 400)
public class StabAbility extends Ability {

  protected StabAbility(Creature executor) {
    super(executor);

    this.addEffect(new StabEffect(this));
  }

  private static class StabEffect extends Effect {

    protected StabEffect(Ability ability) {
      super(ability, EffectTarget.CUSTOM);
    }
    
    @Override
    protected void apply(ICombatEntity entity) {
      super.apply(entity);
      
      final int damage = this.getAbility().getAttributes().value().get();
      entity.hit(damage, this.getAbility());
    }

    @Override
    protected boolean customTarget(ICombatEntity entity) {
      return entity.equals(this.getAbility().getExecutor().getTarget());
    }
  }
}
