package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Entity extends Image {

    private boolean toBeRemoved;
    private Body body;

    public Entity(Texture texture) {
        super(texture);
    }

    public void setToBeRemoved() {
        this.toBeRemoved = true;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    public void attachBody(Body body) {
        this.body = body;
    }

    public void collide(Entity userData) {
System.out.println(getClass().getName());
    }

    public Body getBody() {
        return body;
    }
}
