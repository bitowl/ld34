package de.bitowl.ld34.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.HashMap;

import de.bitowl.ld34.objects.Entity;

public class Box extends PhysicalObject {
    float width, height;
    public Box(float width, float height, HashMap<String, String> attrs) {
        super(attrs);
        this.width = width; this.height = height;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        setShape(shape);
    }

    /**
     * set position bottom left corner (???)
     * @param position
     */
    @Override
    public void setPosition(Vector2 position) {
        super.setPosition(new Vector2(width/2, height/2).add(position));
    }

    @Override
    public void setUserData(Entity userData) {
        super.setUserData(userData);
        userData.setWidth(width);
        userData.setHeight(height);
    }
}
