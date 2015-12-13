package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import de.bitowl.ld34.Utils;
import de.bitowl.ld34.physics.PhysicalObject;

public class Trigger extends Entity {
    private String group;
    private String image;
    private String image_down;
    private boolean triggered;
    private HashMap<String, Array<Pathable>> triggerableObjects;
    public Trigger(String group, String image, String image_down, HashMap<String, Array<Pathable>> triggerableObjects) {
        super(Utils.getDrawable(image));
        this.group = group;
        this.image = image;
        this.image_down = image_down;
        this.triggerableObjects = triggerableObjects;
        System.out.println("CREATED TRIGGER");

    }

    @Override
    public void attachPhysicalObject(PhysicalObject physicalObject) {
        super.attachPhysicalObject(physicalObject);
        // no collision
        physicalObject.getFixtureDef().isSensor = true;
    }

    @Override
    public void collide(Entity userData) {

        System.err.println("==================");

        if (triggered) {
            return;
        }
        if (userData instanceof Player) {
            triggered = true;

            // trigger all objects with the same group
            if (triggerableObjects.containsKey(group)) {
                for (Pathable obj: triggerableObjects.get(group)) {
                    obj.setReceived();
                }
            }

            setDrawable(new TextureRegionDrawable(new TextureRegion(Utils.getDrawable(image_down))));
            // TODO trigger group
        }
    }
}
