package de.gurkenlabs.ldjam46.entities;

import java.awt.Color;

import de.gurkenlabs.ldjam46.gfx.FartEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.AbilityInfo;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.abilities.effects.SoundEffect;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.graphics.OverlayPixelsImageEffect;

@AbilityInfo(name = "FartAbility", cooldown = 5000, range = 0, impact = 60, impactAngle = 360, value = 1, duration = 400, multiTarget = true)
public class FartAbility extends Ability {
  private boolean enabled;

  protected FartAbility(Creature executor) {
    super(executor);

    this.addEffect(new FartEffect(this));
    this.addEffect(new FartCloudEffect(this));
    this.addEffect(new SoundEffect(this, "fart.wav", "fart2.wav", "fart3.wav"));
    this.addEffect(new ScreenShakeEffect(this, 1.5, 1000));
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean canCast() {
    return super.canCast() && isEnabled() && !Farmer.instance().getWaterAbility().isActive();
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
      entity.animations().add(new OverlayPixelsImageEffect(120, new Color(255, 255, 255, 170)));
      Game.loop().perform(130, () -> entity.animations().add(new OverlayPixelsImageEffect(120, new Color(174, 237, 0, 170))));

    }
  }

  private static class FartCloudEffect extends Effect {
    protected FartCloudEffect(Ability ability) {
      super(ability, EffectTarget.EXECUTINGENTITY);
    }

    @Override
    protected void apply(ICombatEntity entity) {
      FartEmitter clouds = new FartEmitter(entity);
      Game.world().environment().add(clouds);
    }
  }

}
