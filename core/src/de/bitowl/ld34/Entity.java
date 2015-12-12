package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Entity extends Image {

    private boolean toBeRemoved;
    private PhysicalObject physicalObject;

    public Entity(Texture texture) {
        super(texture);
        setOrigin(getWidth()/2, getHeight()/2);
    }

    public void setToBeRemoved() {
        this.toBeRemoved = true;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    public void attachPhysicalObject(PhysicalObject physicalObject){
        this.physicalObject = physicalObject;
    }

    public void collide(Entity userData) {
System.out.println(getClass().getName());
    }

    public PhysicalObject getPhysicalObject() {
        return physicalObject;
    }
}
