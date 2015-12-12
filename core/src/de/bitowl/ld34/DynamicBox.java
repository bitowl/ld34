package de.bitowl.ld34;

import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DynamicBox extends DynamicObject {
    public DynamicBox(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);
        setShape(shape);
    }
}
