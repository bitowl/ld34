package de.bitowl.ld34;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class StaticBox extends StaticObject {
    public StaticBox(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);
        setShape(shape);
    }
}
