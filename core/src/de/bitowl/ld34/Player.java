package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.CircleShape;

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
        setWidth(size*2);setHeight(size*2);
        setOrigin(size, size);


    }

    @Override
    public void collide(Entity userData) {
        if (userData instanceof Drop) {
            size++;
            updateSize();
        }
    }
}
