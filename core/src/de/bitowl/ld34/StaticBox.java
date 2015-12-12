package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class StaticBox extends StaticObject {
    float width, height;
    public StaticBox(float width, float height) {
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
}
