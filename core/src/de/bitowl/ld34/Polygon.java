package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.HashMap;

public class Polygon extends PhysicalObject {
    public Polygon(Vector2[] vertices,HashMap<String, String> attrs) {
        super(attrs);
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        setShape(shape);
    }
}
