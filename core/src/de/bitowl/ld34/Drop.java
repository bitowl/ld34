package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;

public class Drop extends Entity {
    public Drop() {
        super(Utils.getDrawable("drop"));
    }

    @Override
    public void collide(Entity userData) {
        System.out.println("DRRRROP");
        setToBeRemoved();
    }
}
