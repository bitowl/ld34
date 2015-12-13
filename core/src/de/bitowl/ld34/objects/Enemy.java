package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.bitowl.ld34.Utils;

public class Enemy extends Entity {

    private String pathName;

    private Array<Vector2> path;
    private float speed = 50;

    private int segment;
    private float onSegment;

    public Enemy(Texture drawable) {
        super(drawable);
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPath(Array<Vector2> path) {
        this.path = path;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (path == null) {
            return; // no moving needs to be done
        }
        Vector2 start = path.get(segment);
        Vector2 end = path.get(segment + 1);

        onSegment+= delta * speed / start.dst(end);
        while (onSegment > 1) {
            segment++;
            if (segment >= path.size -1) {
                segment = 0;
            }
            onSegment-= 1;
            start = path.get(segment);
            end = path.get(segment);
        }

        Vector2 pos  = start.cpy().scl(1 - onSegment).add(end.cpy().scl(onSegment));
        setPosition(pos.x - getOriginX(), pos.y - getOriginY());
        getPhysicalObject().getBody().setTransform(pos.scl(Utils.W2B), 0);

    }

    @Override
    public void collide(Entity userData) {
    }

}
