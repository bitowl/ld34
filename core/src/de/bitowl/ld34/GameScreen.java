package de.bitowl.ld34;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen extends AbstractScreen {

    private final float TIME_STEP = 1 / 60f;
    private final int VELOCITY_ITERATIONS = 6;
    private final int POSITION_ITERATIONS = 2;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private ShapeRenderer shapeRenderer; // custom debug shapes

    private OrthographicCamera camera;

    private SpriteBatch batch;

    private Texture tmp;
    private Body body;


    private final float GRAVITY = 100;
    public GameScreen() {
        world = new World(new Vector2(0, -GRAVITY), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        bodyDef.position.set(100, 300);

        // Create our body in the world using our body definition
        body = world.createBody(bodyDef);


        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(30f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = .3f;
        fixtureDef.friction = 0.6f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();


        body.setGravityScale(0);

        DynamicBox box = new DynamicBox(40, 40);
        box.setPosition(new Vector2(30, 100));
        box.attachTo(world);


        tmp = new Texture("badlogic.jpg");



        // load level
        SVGLoader loader = new SVGLoader(world);
        loader.load("drawing.svg");

    }


    private float angle = -90;

    private final float ROTATE_SPEED = 30;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle += ROTATE_SPEED*delta;
            camera.rotate(ROTATE_SPEED*delta, 0, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle -= ROTATE_SPEED*delta;
            camera.rotate(-ROTATE_SPEED * delta, 0, 0, 1);
        }

        camera.position.set(body.getPosition(), 0);

        camera.update();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            body.applyForceToCenter(7000, 0, true);
        }


        Vector2 newGravity = new Vector2(MathUtils.cos(MathUtils.degRad * angle), MathUtils.sin(MathUtils.degRad * angle));
        // apply personal gravity
        body.applyForceToCenter(newGravity.scl(GRAVITY * body.getMass()), true);


        // System.out.println(new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)));


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(tmp, 0, 0);
        batch.end();

        debugRenderer.render(world, camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        // shapeRenderer.line(0, 100, 300, 100);
        /*System.out.println(body.getPosition());
        System.out.println(body.getPosition().add(newGravity.scl(50 / body.getMass())));
        System.out.println(body.getPosition().add(newGravity.scl(50 / body.getMass())));*/

        Vector2 cenVec = body.getPosition();
        Vector2 aimVec = newGravity.scl(50 / body.getMass() / GRAVITY).add(cenVec);

        shapeRenderer.line(cenVec, aimVec);
        // shapeRenderer.line(new Vector2(100, 306), new Vector2(100, 356));
        shapeRenderer.line(400,400,500,500);

        shapeRenderer.end();

        doPhysicsStep(delta);

    }


    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
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
}
