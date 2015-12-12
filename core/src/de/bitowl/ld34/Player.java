package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class Player extends Entity {

    int size;

    public Player() {
        super(Utils.getDrawable("ball"));
        size = 20;
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
            GameScreen.physicRunnables.add(new Runnable() {
                @Override
                public void run() {
                    DistanceJointDef defJoint = new DistanceJointDef ();
                    defJoint.length = size;
                    defJoint.initialize(getPhysicalObject().getBody(), drop.getPhysicalObject().getBody(),
                            getPhysicalObject().getBody().getPosition(), drop.getPhysicalObject().getBody().getPosition());
                            //new Vector2(getOriginX(),getOriginY()), new Vector2(drop.getOriginX(), drop.getOriginY()));

                    getPhysicalObject().getBody().getWorld().createJoint(defJoint);
                    // make the drop dynamic
                    drop.getPhysicalObject().getBody().setType(BodyDef.BodyType.DynamicBody);
                }
            });

        }
    }
}
