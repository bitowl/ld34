package de.bitowl.ld34.objects;

import com.badlogic.gdx.physics.box2d.Body;

import de.bitowl.ld34.Utils;

public class Exit extends Entity {

    public static final float DEFAULT_WEIGHT = 1;
    private Player player;
    private float weight;
    private String nextLevel;


    public Exit(Player player, float weight, String nextLevel) {
        super(Utils.getDrawable("exit"));
        this.player = player;
        this.weight = weight;
        this.nextLevel = nextLevel;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // TODO only spin if player has enough weight

        if (isOpen()) {
            Body body = getPhysicalObject().getBody();
            body.setTransform(body.getPosition(), body.getAngle() - 15 * delta);
        }
    }



    public boolean isOpen() {
        return player.getSize() >= weight;
    }
    @Override
    public void collide(Entity userData) {
    }

    public String getNextLevel() {
        return nextLevel;
    }
}
