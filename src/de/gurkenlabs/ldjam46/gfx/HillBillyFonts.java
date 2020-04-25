package de.gurkenlabs.ldjam46.gfx;

import java.awt.Font;

import de.gurkenlabs.litiengine.resources.Resources;

public class HillBillyFonts {
  public static final Font MENU = Resources.fonts().get("JANGLYWALK.TTF").deriveFont(48f);
  public static final Font UI = Resources.fonts().get("WoodCabin.ttf").deriveFont(32f);
  public static final Font PIXEL_UI = Resources.fonts().get("old_pixel-7.ttf").deriveFont(10f);
  public static final Font SPEECHBUBBLE = PIXEL_UI.deriveFont(6f);
  public static final Font SPEECHBUBBLE_EMPHASIS = SPEECHBUBBLE.deriveFont(7f);

}
