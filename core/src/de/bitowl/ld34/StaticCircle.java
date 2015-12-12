package de.bitowl.ld34;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class StaticCircle extends PhysicalObject {
    public StaticCircle(float radius) {
        super(BodyDef.BodyType.StaticBody);
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        setShape(circle);
    }
}
