package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class StaticPolygon extends StaticObject {
    public StaticPolygon(Vector2[] vertices) {
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        setShape(shape);
    }
}
