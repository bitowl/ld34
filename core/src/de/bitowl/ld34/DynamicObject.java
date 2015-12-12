package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class DynamicObject {
    private BodyDef def;
    private FixtureDef fixtureDef;
    private Fixture fixture;


    public DynamicObject() {
        // Create our body definition
        def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        fixtureDef = new FixtureDef();
    }


    /**
     * shape will be disposed in attachTo()
     * @param shape
     */
    public void setShape(Shape shape) {
        fixtureDef.shape = shape;
    }

    /**
     * attaches this object to a real world
     * @param world
     */
    public void attachTo(World world) {
        // Create a body from the defintion and add it to the world
        Body groundBody = world.createBody(def);


        fixture = groundBody.createFixture(fixtureDef);
        //fixtureDef.shape.dispose();
    }


    public void setPosition(Vector2 position) {
        def.position.set(position);
    }
}
