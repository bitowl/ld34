package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicalObject {
    private BodyDef def;
    private FixtureDef fixtureDef;
    private Fixture fixture;
    private Body body;
    private Entity userData;


    public PhysicalObject(BodyDef.BodyType type) {
        // Create our body definition
        def = new BodyDef();

        def.type = type;

        fixtureDef = new FixtureDef();
    }


    /**
     * shape will be disposed in attachTo()
     * @param shape
     */
    public void setShape(Shape shape) {
        fixtureDef.shape = shape;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    public void setUserData(Entity userData) {
        this.userData = userData;
    }

    /**
     * attaches this object to a real world
     * @param world
     */
    public void attachTo(World world) {
        // Create a body from the defintion and add it to the world
        body = world.createBody(def);

        if (userData != null) {
            userData.attachBody(body);
        }

        fixture = body.createFixture(fixtureDef);
        //fixtureDef.shape.dispose();

        body.setUserData(userData);
    }


    public void setPosition(Vector2 position) {
        def.position.set(position);
    }
}
