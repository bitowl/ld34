package de.bitowl.ld34.physics;

import com.badlogic.gdx.physics.box2d.CircleShape;

import java.util.HashMap;

import de.bitowl.ld34.Utils;
import de.bitowl.ld34.objects.Entity;

public class Circle extends PhysicalObject {
    private float radius;
    public Circle(float radius, HashMap<String, String> attrs) {
        super( attrs);
        this.radius = radius;
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        setShape(circle);
    }

    @Override
    public void setUserData(Entity userData) {
        super.setUserData(userData);
        userData.setWidth(radius * 2 * Utils.B2W);
        userData.setHeight(radius * 2 * Utils.B2W);
        userData.setOrigin(radius * Utils.B2W, radius*Utils.B2W);
    }
}
