package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.Texture;

import de.bitowl.ld34.Utils;

public class Enemy extends Entity {
    public Enemy(Texture drawable) {
        super(drawable);
    }

    @Override
    public void collide(Entity userData) {
    }

}
