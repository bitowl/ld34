package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Utils {

    public static Texture getDrawable(String name) {
        return new Texture(name + ".png");
    }

    public static BitmapFont font = new BitmapFont();
}
