package de.gurkenlabs.ldjam46.entities;

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
import de.gurkenlabs.litiengine.entities.MapArea;
import de.gurkenlabs.litiengine.entities.MovementInfo;
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

  @Action()
  public void use() {
    if (Game.time().since(this.lastWaterRefill) > WATER_REFILL_DELAY && this.waterAbility.getCharges().get() < this.waterAbility.getCharges().getMax()) {
      Collection<MapArea> refillAreas = Game.world().environment().getByTag(MapArea.class, "refillwater");
      for (MapArea area : refillAreas) {
        if (area.getBoundingBox().intersects(this.getCollisionBox())) {
          this.waterAbility.getCharges().setToMax();
          System.out.println("water refilled");
        }
      }
    }

    if (this.waterAbility.canCast()) {
      this.waterAbility.cast();
    } else if (!this.waterAbility.isOnCooldown() && this.waterAbility.getCharges().get() == 0 && !speechbubbleActive) {

      SpeechBubble bubble = SpeechBubble.create(this, "need to refill ma can", GameManager.SPEECHBUBBLE_APPEARANCE, GameManager.SPEECHBUBBLE_FONT);
      speechbubbleActive = true;
      bubble.addListener(() -> {
        speechbubbleActive = false;
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

  @Override
  protected IMovementController createMovementController() {
    return new FarmerController(this);
  }
}