package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.AbilityInfo;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@AbilityInfo(name = "FartAbility", cooldown = 5000, range = 0, impact = 30, impactAngle = 360, value = 1, duration = 400)
public class FartAbility extends Ability {

  protected FartAbility(Creature executor) {
    super(executor);

    this.addEffect(new FartEffect(this));
  }

  private static class FartEffect extends Effect {

    protected FartEffect(Ability ability) {
      super(ability, EffectTarget.ENEMY);
    }

    @Override
    protected void apply(ICombatEntity entity) {
      super.apply(entity);

      if (!(entity instanceof EnemyFarmer)) {
        return;
      }

      EnemyFarmer farmer = (EnemyFarmer) entity;
      farmer.fartOn();
    }
  }
}
