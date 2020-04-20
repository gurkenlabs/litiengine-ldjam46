package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.AnimationInfo;
import de.gurkenlabs.litiengine.entities.Prop;

@AnimationInfo(spritePrefix = "prop-can")
public class Can extends Prop {

  public Can() {
    super("can");
  }

  @Override
  public String sendMessage(Object sender, String message) {
    if (message.equals("picked-up")) {
      Game.world().environment().remove(this);
      Game.world().environment().remove("canlight");
      Farmer.instance().setHasCan(true);
    }
    return super.sendMessage(sender, message);
  }
}
