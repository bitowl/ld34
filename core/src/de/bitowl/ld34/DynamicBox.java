package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DynamicBox extends DynamicObject {
    private float width, height;

    public DynamicBox(float width, float height) {
        this.width = width; this.height = height;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        setShape(shape);
    }

    @Override
    public void setPosition(Vector2 position) {
        super.setPosition(new Vector2(-width,-height).add(position));
    }
}
