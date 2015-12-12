package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import de.bitowl.ld34.Utils;
import de.bitowl.ld34.physics.PhysicalObject;

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

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Utils.font.draw(batch, "mass: " + physicalObject.getBody().getMass()+"\ndensity: " + physicalObject.getBody().getFixtureList().get(0).getDensity(),getX(), getY());
    }

    public PhysicalObject getPhysicalObject() {
        return physicalObject;
    }
}
