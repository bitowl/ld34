package de.bitowl.ld34.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.bitowl.ld34.Utils;

public class Pathable extends Entity {
    public Pathable(Texture texture) {
        super(texture);
    }

    private String pathName;

    private Array<Vector2> path;
    private float speed = 50;

    private int segment;
    private float onSegment;


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

        if ((getReceiverGroup() != null && !hasReceived()) || segment == -1) {
            System.out.println(getReceiverGroup() + " not yet: " + segment + " | " + hasReceived());
            if (segment == 0) { // stay at first point
                Vector2 pos = path.get(0).cpy();
                System.out.println(pos);
                setPosition(pos.x - getOriginX(), pos.y - getOriginY());
                getPhysicalObject().getBody().setTransform(pos.scl(Utils.W2B), getPhysicalObject().getBody().getAngle());
            }
            return; // not yet received
        }
        Vector2 start = path.get(segment).cpy();
        Vector2 end = path.get(segment + 1).cpy();

        onSegment+= delta * speed / start.dst(end);
        while (onSegment > 1) {
            segment++;
            if (segment >= path.size -1) {
                if (getReceiverGroup() != null) {
                    segment = -1; // it's a door -> STOP
                    return;
                } else {
                    segment = 0; // it's an enemy -> loop
                }

            }
            onSegment-= 1;
            start = path.get(segment);
            end = path.get(segment + 1);
        }

        Vector2 pos  = start.cpy().scl(1 - onSegment).add(end.cpy().scl(onSegment));
        setPosition(pos.x - getOriginX(), pos.y - getOriginY());
        getPhysicalObject().getBody().setTransform(pos.scl(Utils.W2B), getPhysicalObject().getBody().getAngle());

    }

}
