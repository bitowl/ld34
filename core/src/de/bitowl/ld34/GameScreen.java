package de.bitowl.ld34;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

import de.bitowl.ld34.objects.Entity;
import de.bitowl.ld34.objects.Player;
import de.bitowl.ld34.physics.PhysicalObject;

public class GameScreen extends AbstractScreen {

    public static Array<Runnable> physicRunnables;
    private final float TIME_STEP = 1 / 60f;
    private final int VELOCITY_ITERATIONS = 6;
    private final int POSITION_ITERATIONS = 2;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Viewport debugViewport;
    private ShapeRenderer shapeRenderer; // custom debug shapes

    private OrthographicCamera camera;


    private Stage stage;
    private Body body;


    private final float GRAVITY = 10;

    private ParticleEffect bubblesEffect;


    public GameScreen() {
        physicRunnables = new Array<Runnable>();

        world = new World(new Vector2(0, -GRAVITY), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();


        stage = new Stage(new FitViewport(600 , 900));
        debugViewport = new FitViewport(600 * Utils.W2B, 900 * Utils.W2B);

        camera = (OrthographicCamera) stage.getCamera();


        HashMap<String, String> attrs = new HashMap<String, String>();
        attrs.put("dyn", "");
        attrs.put("mass", "20");
        PhysicalObject obj = new PhysicalObject(attrs);
        CircleShape circle = new CircleShape();
        circle.setRadius(20f * Utils.W2B);
        obj.setPosition(new Vector2(200 * Utils.W2B, 200 * Utils.W2B));

        obj.setShape(circle);
        FixtureDef fixtureDef = obj.getFixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.6f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit


        Player player = new Player();

        // Entity obj = new Entity(new Texture("ball.png"));
        // obj.setOrigin(obj.getWidth()/2, obj.getHeight()/2);
        stage.addActor(player);
        obj.setUserData(player);

        player.attachPhysicalObject(obj);











        // load level
        SVGLoader loader = new SVGLoader(world, stage, player);
        loader.load("level/lvl1.svg");

        world.setContactListener(new ExtremeContactListener());


        // finish player initialisation
        obj.attachTo(world);
        body = obj.getBody();
        body.setGravityScale(0);
        body.setAngularDamping(2);
        body.setLinearDamping(.5f);
        player.updateSize(player.getSize());
        player.toFront();




        // PARTICLES

    }


    private float angle = -90;

    private final float ROTATE_SPEED = 180;

    @Override
    public void render(float delta) {
        //// MOVE & INPUT ////



        Vector2 newGravity = new Vector2(MathUtils.cos(MathUtils.degRad * angle), MathUtils.sin(MathUtils.degRad * angle));
        // apply personal gravity
        body.applyForceToCenter(newGravity.scl(GRAVITY * body.getMass()), true);


        doPhysicsStep(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle += ROTATE_SPEED*delta;
            camera.rotate(ROTATE_SPEED*delta, 0, 0, 1);
            debugViewport.getCamera().rotate(ROTATE_SPEED * delta, 0, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle -= ROTATE_SPEED*delta;
            camera.rotate(-ROTATE_SPEED * delta, 0, 0, 1);
            debugViewport.getCamera().rotate(-ROTATE_SPEED * delta, 0, 0, 1);
        }


        System.out.println("->" + body.getPosition());
        camera.position.set(body.getPosition().scl(Utils.B2W), 0);
        debugViewport.getCamera().position.set(body.getPosition(), 0);

        camera.update();
        debugViewport.getCamera().update();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            body.applyForceToCenter(7000, 0, true);
        }


        //// RENDER ////

        Gdx.gl.glClearColor(0.87f, 0.86f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // TODO don't create a new one every time
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body b : bodies) {
            // Get the body's user data - in this example, our user
            // data is an instance of the Entity class
            Entity e = (Entity) b.getUserData();

            if (e != null) {
                // Update the entities/sprites position and angle
                e.setPosition(b.getPosition().x * Utils.B2W - e.getOriginX(), b.getPosition().y* Utils.B2W - e.getOriginY());
                // We need to convert our angle from radians to degrees
                e.setRotation(MathUtils.radiansToDegrees * b.getAngle());
            }
        }


        // System.out.println(new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)));


        // remove dead people
        for (Actor actor:stage.getActors()) {
            if (actor instanceof Entity) {
                Entity entity = (Entity) actor;
                if (entity.isToBeRemoved()) {
                    entity.remove();
                    world.destroyBody(entity.getPhysicalObject().getBody());
                }
            }
        }

        // execute everything that might interfere with the physics
        for (Runnable runnable : physicRunnables) {
            runnable.run();
        }
        physicRunnables.clear();

        stage.act(delta);
        stage.draw();

        debugRenderer.render(world, debugViewport.getCamera().combined);

        shapeRenderer.setProjectionMatrix(debugViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        // shapeRenderer.line(0, 100, 300, 100);
        /*System.out.println(body.getPosition());
        System.out.println(body.getPosition().add(newGravity.scl(50 / body.getMass())));
        System.out.println(body.getPosition().add(newGravity.scl(50 / body.getMass())));*/

       /* Vector2 cenVec = body.getPosition();
        Vector2 aimVec = newGravity.scl(50 / body.getMass() / GRAVITY).add(cenVec);

        shapeRenderer.line(cenVec, aimVec);*/
        // shapeRenderer.line(new Vector2(100, 306), new Vector2(100, 356));
        // shapeRenderer.line(400,400,500,500);

        shapeRenderer.end();


    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        debugViewport.update(width, height);
        // camera.setToOrtho(false, width, height);
    }

    private float accumulator = 0;

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    class ExtremeContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {

            if (contact.getFixtureA().getBody().getUserData() instanceof Player) {
                ((Player) contact.getFixtureA().getBody().getUserData()).simpleContact(contact);
            }
            if (contact.getFixtureB().getBody().getUserData() instanceof Player) {
                ((Player) contact.getFixtureB().getBody().getUserData()).simpleContact(contact);
            }


            if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() != null) {
                ((Entity)contact.getFixtureA().getBody().getUserData()).collide((Entity) contact.getFixtureB().getBody().getUserData());
                ((Entity)contact.getFixtureB().getBody().getUserData()).collide((Entity) contact.getFixtureA().getBody().getUserData());
            }
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }

}
