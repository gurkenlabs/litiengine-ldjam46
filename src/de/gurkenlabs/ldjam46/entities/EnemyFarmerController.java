package de.gurkenlabs.ldjam46.entities;

import java.awt.Color;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.behavior.AStarGrid;
import de.gurkenlabs.litiengine.entities.behavior.AStarPathFinder;
import de.gurkenlabs.litiengine.entities.behavior.EntityNavigator;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;
import de.gurkenlabs.litiengine.physics.MovementController;

public class EnemyFarmerController extends MovementController<EnemyFarmer> {

  private static final int NAVIGATE_DELAY = 500;
  private static final int FART_RUN_DELAY = 1000;
  private final int pumpkinDist;

  private EntityNavigator navi;
  private long lastNavigate;

  private long initialFart;

  public EnemyFarmerController(EnemyFarmer mobileEntity) {
    super(mobileEntity);

    this.pumpkinDist = Game.random().nextInt(10, 20);
    AStarGrid grid = GameManager.getCurrentGrid();

    this.navi = new EntityNavigator(mobileEntity, new AStarPathFinder(grid));
  }

  @Override
  public void update() {
    super.update();

    if (this.getEntity().getTarget() == null) {
      return;
    }

    if (this.getEntity().isFartedOn()) {
      if (initialFart == 0) {
        if (this.navi.isNavigating()) {
          this.navi.stop();
        }

        this.initialFart = Game.loop().getTicks();
        SpeechBubble bubble = SpeechBubble.create(this.getEntity(), "...", new SpeechBubbleAppearance(Color.WHITE, new Color(0, 0, 0, 100), new Color(0, 0, 0, 0), 2), GameManager.GUI_FONT.deriveFont(5f));
      } else {
        double dist = this.getEntity().getSpawn().getCenter().distance(this.getEntity().getCenter());
        if (dist <= pumpkinDist) {
          Game.world().environment().remove(this.getEntity());
          return;
        }
        if (Game.time().since(initialFart) > FART_RUN_DELAY && !this.navi.isNavigating()) {
          this.navi.navigate(this.getEntity().getSpawn().getCenter());
          this.getEntity().setVelocity(this.getEntity().getVelocity().get() * 1.75f);
          // TODO: this.getEntity().animations().getCurrent().setDurationForAllKeyFrames(80);
        }
      }

      return;
    }

    if (this.getEntity().getTarget().isDead()) {
      this.getEntity().updateTarget();

      if (this.getEntity().getTarget() == null) {
        return;
      }
    }

    if (Game.time().since(this.lastNavigate) < NAVIGATE_DELAY) {
      return;
    }

    double dist = this.getEntity().getTarget().getCenter().distance(this.getEntity().getCenter());
    if (dist > pumpkinDist && !this.navi.isNavigating()) {

      this.navi.navigate(this.getEntity().getTarget().getCenter());
    } else {
      if (this.navi.isNavigating()) {
        this.navi.stop();
      }

      if (this.getEntity().getStabAbility().canCast()) {
        this.getEntity().getStabAbility().cast();
      }
    }
  }
}
