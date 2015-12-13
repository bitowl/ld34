package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.bitowl.ld34.Utils;

public class Enemy extends Pathable {


    public Enemy(Texture drawable) {
        super(drawable);
    }

    @Override
    public void collide(Entity userData) {
    }

}
