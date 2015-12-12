package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {

    int size;
    Array<Drop> connectedDrops;

    public Player() {
        super(Utils.getDrawable("ball"));
        size = 20;
        connectedDrops = new Array<Drop>();
    }



    public void updateSize() {
        CircleShape circle = new CircleShape();
        circle.setRadius(size);
        getPhysicalObject().changeShape(circle);
        setWidth(size*2);setHeight(size * 2);
        setOrigin(size, size);


    }

    @Override
    public void collide(Entity userData) {
        if (userData instanceof Drop) {

            // size++;
            // updateSize();
            final Drop drop = (Drop) userData;
            if (drop.isAttachedToPlayer()) {return;}
            // connectedDrops.add(drop);
            GameScreen.physicRunnables.add(new Runnable() {
                @Override
                public void run() {
                    /*RopeJointDef defJoint = new RopeJointDef();
                    defJoint.bodyA = getPhysicalObject().getBody();
                    defJoint.bodyB = drop.getPhysicalObject().getBody();
                    defJoint.maxLength = size + drop.getRadius();*/

                    DistanceJointDef defJoint = new DistanceJointDef ();
                    defJoint.length = size + drop.getRadius();
                    defJoint.initialize(getPhysicalObject().getBody(), drop.getPhysicalObject().getBody(), getPhysicalObject().getBody().getPosition(), drop.getPhysicalObject().getBody().getPosition());

                    getPhysicalObject().getBody().getWorld().createJoint(defJoint);
                    // make the drop dynamic
                    drop.getPhysicalObject().getBody().setType(BodyDef.BodyType.DynamicBody);
                }
            });

        }
    }
}
