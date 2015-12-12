package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class StaticObject {

    private BodyDef def;
    private Shape shape;

    public StaticObject() {
        // Create our body definition
        def = new BodyDef();
    }


    /**
     * shape will be disposed in attachTo()
     *
     * @param shape
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * attaches this object to a real world
     *
     * @param world
     */
    public void attachTo(World world) {
        // Create a body from the defintion and add it to the world
        Body groundBody = world.createBody(def);

        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(shape, 0.0f);
        shape.dispose();
    }


    public void setPosition(Vector2 position) {
        def.position.set(position);
    }
}
