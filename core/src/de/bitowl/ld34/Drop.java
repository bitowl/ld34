package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Drop extends Entity {
    private boolean attachedToPlayer;
    private float radius;
    public Drop(float r) {
        super(Utils.getDrawable("drop"));
        this.radius = r;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public void collide(Entity userData) {
        System.out.println("DRRRROP");
    //    setToBeRemoved();
    }

    public boolean isAttachedToPlayer() {
        return attachedToPlayer;
    }

    public void setAttachedToPlayer(boolean attachedToPlayer) {
        this.attachedToPlayer = attachedToPlayer;
    }
}
