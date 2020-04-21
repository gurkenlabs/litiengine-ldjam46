package de.gurkenlabs.ldjam46.gfx;

import java.awt.geom.Point2D;

import de.gurkenlabs.ldjam46.entities.EnemyFarmer;
import de.gurkenlabs.ldjam46.entities.Farmer;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.EntityMovedEvent;
import de.gurkenlabs.litiengine.entities.IMobileEntity.EntityMovedListener;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.emitters.AnimationEmitter;
import de.gurkenlabs.litiengine.resources.Resources;

public class WalkDustSpawner implements EntityMovedListener {
  private long lastWalkDust;

  @Override
  public void moved(EntityMovedEvent event) {
    if (event.getEntity() instanceof EnemyFarmer && event.getEntity().getVelocity().get() <= 70) {
      return;
    }
    
    if (event.getEntity() instanceof Farmer && event.getEntity().getVelocity().get() < 70) {
      return;
    }

    final int STEP_DELAY = event.getEntity() instanceof EnemyFarmer ? 200 : 360;
    if (event.getDeltaX() == 0 && event.getDeltaY() == 0 || Game.world().environment() == null) {
      return;
    }
    Spritesheet walkDustSprite = Resources.spritesheets().get("walk-dust");
    if (Game.time().since(this.lastWalkDust) < STEP_DELAY) {
      return;
    }

    this.lastWalkDust = Game.loop().getTicks();

    Point2D walkLocation = new Point2D.Double(event.getEntity().getCollisionBoxCenter().getX() - walkDustSprite.getSpriteWidth() / 2.0, event.getEntity().getCollisionBoxCenter().getY() - walkDustSprite.getSpriteHeight() / 2.0);
    AnimationEmitter walkDust = new AnimationEmitter(walkDustSprite, walkLocation);
    walkDust.setRenderType(RenderType.NORMAL);
    Game.world().environment().add(walkDust);
  }

}
