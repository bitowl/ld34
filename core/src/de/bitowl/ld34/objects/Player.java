package de.bitowl.ld34.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.CircleShape;

import de.bitowl.ld34.GameScreen;
import de.bitowl.ld34.Utils;

public class Player extends Entity {

    float size;

    public Player() {
        super(Utils.getDrawable("ball"));
        size = 20 * Utils.W2B;
    }



    public void updateSize() {
        CircleShape circle = new CircleShape();
        circle.setRadius(size);
        getPhysicalObject().changeShape(circle);
        setWidth(size*2 * Utils.B2W);setHeight(size * 2* Utils.B2W);
        setOrigin(size* Utils.B2W, size* Utils.B2W);
        GameScreen.physicRunnables.add(new Runnable() {
            @Override
            public void run() {
                getPhysicalObject().changeMass(size);
            }
        });


    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void collide(Entity userData) {
        if (userData instanceof Drop) {
            Drop drop = (Drop) userData;

            size = (float)Math.sqrt(size * size + drop.getSize() *drop.getSize());
            updateSize();
        }
    }
}
