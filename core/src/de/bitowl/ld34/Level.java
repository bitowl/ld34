package de.bitowl.ld34;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;

import de.bitowl.ld34.objects.Entity;
import de.bitowl.ld34.objects.Player;
import de.bitowl.ld34.physics.PhysicalObject;

public class Level {

    Stage stage;
    Player player;
    World world;

    final float GRAVITY = 10;

    final int WIDTH = 600;
    final int HEIGHT = 900;

    private String name;
    public Level(String name) {
        this.name = name;
    }


    public void init() {

        stage = new Stage(new FitViewport(WIDTH , HEIGHT));


        world = new World(new Vector2(0, -GRAVITY), true);

        // create the player object
        player = new Player();

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




        // Entity obj = new Entity(new Texture("ball.png"));
        // obj.setOrigin(obj.getWidth()/2, obj.getHeight()/2);
        stage.addActor(player);
        obj.setUserData(player);

        player.attachPhysicalObject(obj);











        // load level
        SVGLoader loader = new SVGLoader(world, stage, player);
        loader.load("level/" + name + ".svg");

        world.setContactListener(new ExtremeContactListener());


        // finish player initialisation
        obj.attachTo(world);
        Body body = obj.getBody();
        body.setGravityScale(0);
        body.setAngularDamping(2);
        body.setLinearDamping(.5f);
        player.updateSize(player.getSize());
        player.toFront();


    }

    public void actNdraw(float delta) {
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

        stage.act(delta);
        stage.draw();

    }



}
