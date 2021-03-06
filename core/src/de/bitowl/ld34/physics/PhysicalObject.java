package de.bitowl.ld34.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;

import de.bitowl.ld34.Utils;
import de.bitowl.ld34.objects.Entity;
import de.bitowl.ld34.GameScreen;

public class PhysicalObject {
    private BodyDef def;
    private FixtureDef fixtureDef;
    private Fixture fixture;
    private Body body;
    private Entity userData;
    private HashMap<String, String> attrs;


    public PhysicalObject(HashMap<String, String> attrs) {
        this.attrs = attrs;


        // Create our body definition
        def = new BodyDef();
        fixtureDef = new FixtureDef();

        if (attrs.containsKey("dyn")) {
            def.type = BodyDef.BodyType.DynamicBody;
            fixtureDef.density = 1f; // default density
        } else {
            def.type = BodyDef.BodyType.StaticBody;
        }


        fixtureDef.friction = .6f;
        fixtureDef.restitution = 0;
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


        if (attrs.containsKey("mass")) {
            changeMass(Float.parseFloat(attrs.get("mass")));
        }
        body.setAwake(true);

    }

    /**
     * adjust the density so that the mass has the given value
     * @param mass
     */
    public void changeMass(float mass) {
        fixture.setDensity(mass / body.getMass() * fixture.getDensity());
        body.resetMassData();
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

    public void setRotation(float degrees) {
        def.angle = MathUtils.degRad * degrees;
    }

    public Body getBody() {
        return body;
    }
}
