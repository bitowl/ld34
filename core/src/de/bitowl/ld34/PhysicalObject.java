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
     * set the shape before calling attachTo()
     *
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
            userData.attachPhysicalObject(this);
        }

        fixture = body.createFixture(fixtureDef);
        //fixtureDef.shape.dispose();

        body.setUserData(userData);
    }

    /**
     * to change the shape after attachTo was called
     * shape will be disposed in here
     */
    public void changeShape(final Shape shape) {

        GameScreen.physicRunnables.add(new Runnable() {

            @Override
            public void run() {
                // dispose all old fixtures
                for (Fixture fixture : body.getFixtureList()) {
                    body.destroyFixture(fixture);
                }


                fixtureDef.shape = shape;
                fixture = body.createFixture(fixtureDef);

                shape.dispose();

            }
        });
    }


    public void setPosition(Vector2 position) {
        def.position.set(position);
    }

    public Body getBody() {
        return body;
    }
}
