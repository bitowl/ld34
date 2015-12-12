package de.bitowl.ld34.objects;

import com.badlogic.gdx.physics.box2d.CircleShape;

import de.bitowl.ld34.GameScreen;
import de.bitowl.ld34.Utils;

public class Player extends Entity {

    float size;

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
            size += drop.getSize()/2;
            updateSize();
        }
    }
}
