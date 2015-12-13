package de.bitowl.ld34;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
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
    private Box2DDebugRenderer debugRenderer;
    private Viewport debugViewport;

    private OrthographicCamera camera;


    public static boolean cutScene; // cut scene disables physics

    private Level level; // current level
    private Array<Body> bodies;


    private float angle = -90;
    private final float ROTATE_SPEED = 180;

    private Image darkImage;
    private Table pauseDialog;
    private boolean pause;

    public GameScreen() {
        this("lvl1");
    }

    public GameScreen(String startLevel) {
        instance = this;

        physicRunnables = new Array<Runnable>();

        debugRenderer = new Box2DDebugRenderer();




        pauseDialog = new Table();
        darkImage = new Image(Utils.getDrawable("dark"));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(Utils.get9Patch("button_up",29), Utils.get9Patch("button_down",29), null, Utils.font);
        TextButton contin = new TextButton("continue", style);
        contin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                pause = false;
                pauseDialog.remove();
                darkImage.remove();
            }
        });
        pauseDialog.add(contin).pad(10).padBottom(20).colspan(2).row();

        TextButton restart = new TextButton("restart", style);
        restart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                pause = false;
                pauseDialog.remove();
                darkImage.remove();
                restartLevel();
            }
        });
        pauseDialog.add(restart).pad(10);

        TextButton menu = new TextButton("menu", style);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                MyGame.switchTo(new MenuScreen());
            }
        });
        pauseDialog.add(menu).pad(10);



        switchLevel(startLevel);
    }


    @Override
    public void show() {
        Utils.startMusic("test2");

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        //// MOVE & INPUT ////



        if (pause) {
            Gdx.gl.glClearColor(0.87f, 0.86f, 0.9f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            level.stage.draw();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
                pause = false;
                pauseDialog.remove();
                darkImage.remove();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            pause = true;
            level.stage.addActor(darkImage);
            level.stage.addActor(pauseDialog);
            pauseDialog.setX(camera.position.x);
            pauseDialog.setY(camera.position.y);
            darkImage.setSize(level.stage.getWidth() * 2, level.stage.getHeight() * 2);
            darkImage.setX(camera.position.x - level.stage.getWidth());
            darkImage.setY(camera.position.y - level.stage.getHeight());
            Gdx.input.setInputProcessor(level.stage);
        }

        Vector2 newGravity = new Vector2(MathUtils.cos(MathUtils.degRad * angle), MathUtils.sin(MathUtils.degRad * angle));
        // apply personal gravity
        Body body = level.player.getPhysicalObject().getBody();
        body.applyForceToCenter(newGravity.scl(level.GRAVITY * body.getMass()), true);


        if (!cutScene) {
            doPhysicsStep(delta);
        }

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

        Vector2 pos = body.getPosition();
        if (cutScene) {
            pos.x = (level.player.getX()+level.player.getOriginX()) * Utils.W2B;
            pos.y = (level.player.getY() + level.player.getOriginY()) * Utils.W2B;
        }
        debugViewport.getCamera().position.set(pos, 0);
        camera.position.set(pos.scl(Utils.B2W), 0);



        camera.update();
        debugViewport.getCamera().update();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            body.applyForceToCenter(7000, 0, true);
        }


        //// RENDER ////

        Gdx.gl.glClearColor(0.87f, 0.86f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);



        if (!cutScene) {
            for (Body b : bodies) {
                // Get the body's user data - in this example, our user
                // data is an instance of the Entity class
                Entity e = (Entity) b.getUserData();

                if (e != null) {
                    // Update the entities/sprites position and angle
                    e.setPosition(b.getPosition().x * Utils.B2W - e.getOriginX(), b.getPosition().y * Utils.B2W - e.getOriginY());
                    // We need to convert our angle from radians to degrees
                    e.setRotation(MathUtils.radiansToDegrees * b.getAngle());
                }
            }


            // System.out.println(new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)));


            // execute everything that might interfere with the physics
            for (Runnable runnable : physicRunnables) {
                runnable.run();
            }
            physicRunnables.clear();

        }

        level.actNdraw(delta);



        // System.out.println(Gdx.graphics.getFramesPerSecond());
        if (!cutScene) {
         //   debugRenderer.render(level.world, debugViewport.getCamera().combined);
        }
    }


    @Override
    public void resize(int width, int height) {
        level.stage.getViewport().update(width, height);
        level.stage.getViewport().apply();
        level.stage.getCamera().update();

        debugViewport.update(width,height);
        debugViewport.apply();
    }

    private float accumulator = 0;

    private void doPhysicsStep(float deltaTime) {
        if (level == null) {return;}
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            level.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }


    private String currentLevel;
    public void restartLevel() {
        switchLevel(currentLevel);
    }
    public void switchLevel(String name) {

        currentLevel = name;
        if (level != null) {
            level.dispose();
        }

        if (name.equals("menu")) {
            System.err.println("MENNNUU");
            MyGame.switchTo(new MenuScreen());
            return;
        }

        cutScene = false; // we have to synchronize the objects with their physics first
        level = new Level(name);
        level.init();

        debugViewport = new FillViewport(level.WIDTH * Utils.W2B, level.HEIGHT * Utils.W2B);

        // get a list of bodies
        bodies = new Array<Body>();
        level.world.getBodies(bodies);
        camera = (OrthographicCamera) level.stage.getCamera();

        angle = -90;

    }

    private static GameScreen instance;
    public static GameScreen get() {
        return instance;
    }
}
