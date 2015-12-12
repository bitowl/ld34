package de.bitowl.ld34.physics;

import com.badlogic.gdx.physics.box2d.CircleShape;

import java.util.HashMap;

public class Circle extends PhysicalObject {
    public Circle(float radius, HashMap<String, String> attrs) {
        super( attrs);
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        setShape(circle);
    }
}
