package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;

public class Drop extends Entity {
    private float size;
    public Drop(float size) {
        super(Utils.getDrawable("drop"));
        this.size = size;
    }

    @Override
    public void collide(Entity userData) {
        System.out.println("DRRRROP");
        setToBeRemoved();
    }

    public float getSize() {
        return size;
    }
}
