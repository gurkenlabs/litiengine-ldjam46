package de.gurkenlabs.ldjam46.entities;

import java.awt.Color;
import java.util.Collection;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.gfx.WalkDustSpawner;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.entities.Action;
import de.gurkenlabs.litiengine.entities.AnimationInfo;
import de.gurkenlabs.litiengine.entities.CollisionInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.EntityInfo;
import de.gurkenlabs.litiengine.entities.LightSource;
import de.gurkenlabs.litiengine.entities.MapArea;
import de.gurkenlabs.litiengine.entities.MovementInfo;
import de.gurkenlabs.litiengine.entities.Trigger;
import de.gurkenlabs.litiengine.graphics.CreatureShadowImageEffect;
import de.gurkenlabs.litiengine.graphics.animation.Animation;
import de.gurkenlabs.litiengine.graphics.animation.CreatureAnimationController;
import de.gurkenlabs.litiengine.graphics.animation.IEntityAnimationController;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.physics.IMovementController;

@EntityInfo(width = 11, height = 20)
@CollisionInfo(collision = true, collisionBoxWidth = 8, collisionBoxHeight = 8, align = Align.CENTER, valign = Valign.DOWN)
@MovementInfo(velocity = 70)
@AnimationInfo(spritePrefix = "keeper")
public class Farmer extends Creature {
  private static final int WATER_REFILL_DELAY = 1000;

  private static Farmer instance;

  private final WaterAbility waterAbility = new WaterAbility(this);

  private final FartAbility fartAbility = new FartAbility(this);

  private long lastWaterRefill;
  private boolean hasCan;

  private Farmer() {
    this.onMoved(new WalkDustSpawner());
  }

  public static Farmer instance() {
    if (instance == null) {
      instance = new Farmer();
    }

    return instance;
  }

  private boolean speechbubbleActive;
  private boolean grabSpeechbubbleActive;
  public boolean firstRefillEver = true;

  public boolean movementBlocked;

  @Override
  public String getSpritesheetName() {
    return this.hasCan() ? "keepercan" : "keeper";
  }

  @Action()
  public void use() {
    if (!this.hasCan()) {
      Trigger trigger = Game.world().environment().interact(this);
    }

    if (this.hasCan() && Game.time().since(this.lastWaterRefill) > WATER_REFILL_DELAY
        && this.waterAbility.getCharges().get() < this.waterAbility.getCharges().getMax()) {
      Collection<MapArea> refillAreas = Game.world().environment().getByTag(MapArea.class, "refillwater");
      for (MapArea area : refillAreas) {
        if (area.getBoundingBox().intersects(this.getCollisionBox())) {
          this.waterAbility.getCharges().setToMax();

          for (LightSource light : Game.world().environment().getByTag(LightSource.class, "fountainlight")) {
            light.deactivate();
          }

          if (firstRefillEver) {
            SpeechBubble.create(this, "Hurry! Mah pumpkins need water!",
                GameManager.SPEECHBUBBLE_APPEARANCE, GameManager.SPEECHBUBBLE_FONT);

            Game.loop().perform(500, () -> {
              for (LightSource l : Game.world().environment().getByTag(LightSource.class, "pumpkinlight")) {
                l.activate();
              }
            });
          }

          firstRefillEver = false;
          System.out.println("water refilled");
        }
      }
    }

    if (this.waterAbility.canCast()) {
      this.waterAbility.cast();
    } else if (this.hasCan() && !this.waterAbility.isOnCooldown() && this.waterAbility.getCharges().get() == 0
        && !speechbubbleActive) {

      String text = firstRefillEver ? "I need to use the fountain to refill ma can!" : "Need to refill ma can...";
      SpeechBubble bubble = SpeechBubble.create(this, text,
          GameManager.SPEECHBUBBLE_APPEARANCE, GameManager.SPEECHBUBBLE_FONT);
      speechbubbleActive = true;
      for (LightSource light : Game.world().environment().getByTag(LightSource.class, "fountainlight")) {
        light.activate();
      }

      bubble.addListener(() -> {
        speechbubbleActive = false;
      });
    } else if (!this.hasCan() && !grabSpeechbubbleActive) {
      SpeechBubble bubble = SpeechBubble.create(this, "I need to grab ma can first!",
          GameManager.SPEECHBUBBLE_APPEARANCE, GameManager.SPEECHBUBBLE_FONT);
      grabSpeechbubbleActive = true;
      bubble.addListener(() -> {
        grabSpeechbubbleActive = false;
      });
    }
  }

  @Action
  public void fart() {
    this.fartAbility.cast();
  }

  public WaterAbility getWaterAbility() {
    return this.waterAbility;
  }

  public FartAbility getFartAbility() {
    return this.fartAbility;
  }

  @Override
  protected IMovementController createMovementController() {
    return new FarmerController(this);
  }

  @Override
  protected IEntityAnimationController<?> createAnimationController() {
    IEntityAnimationController<?> controller = new CreatureAnimationController<>(this, false);
    controller.add(new Animation("keepercan-walk-up", true, true));
    controller.add(new Animation("keepercan-walk-down", true, true));
    controller.add(new Animation("keepercan-idle", true, true));

    controller.add(new Animation("keeper-celebrate", true, true));
    controller.add(new Animation("keeper-pout", true, true));
    controller.addRule(x -> Farmer.instance().isIdle() && !Farmer.instance().hasCan(), x -> "keeper-idle");
    controller.addRule(x -> Farmer.instance().isIdle() && Farmer.instance().hasCan(), x -> "keepercan-idle");
    controller.addRule(x -> GameManager.isHarvesting(), x -> "keeper-celebrate");
    controller.addRule(x -> GameManager.isLevelFailed(), x -> "keeper-pout");

    CreatureShadowImageEffect effect = new CreatureShadowImageEffect(this, new Color(24, 30, 28, 100));
    effect.setOffsetY(1);
    controller.add(effect);
    return controller;
  }

  public boolean hasCan() {
    return hasCan;
  }

  public void setHasCan(boolean hasCan) {
    this.hasCan = hasCan;
  }
}