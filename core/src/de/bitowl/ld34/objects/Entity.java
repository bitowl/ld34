package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import de.bitowl.ld34.Utils;
import de.bitowl.ld34.physics.PhysicalObject;

public class Entity extends Image {

    private boolean toBeRemoved;
    private PhysicalObject physicalObject;

    private String receiverGroup;
    private boolean received;

    public Entity(Texture texture) {
        super(texture);
    }

    /*@Override
    public void setWidth(float width) {
        super.setWidth(width*Utils.B2W);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height*Utils.B2W);
    }*/

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
        /*Utils.font.draw(batch, "mass: " + physicalObject.getBody().getMass()+
                "\ndensity: " + physicalObject.getBody().getFixtureList().get(0).getDensity()
                +"\nspeed: " + physicalObject.getBody().getLinearVelocity().y
                ,getX(), getY());*/
    }

    public PhysicalObject getPhysicalObject() {
        return physicalObject;
    }


    public void setReceived() {
        this.received = true;
    }

    public void setReceiverGroup(String receiverGroup) {
        this.receiverGroup = receiverGroup;
    }

    public boolean hasReceived() {
        return received;
    }

    public String getReceiverGroup() {
        return receiverGroup;
    }
}
