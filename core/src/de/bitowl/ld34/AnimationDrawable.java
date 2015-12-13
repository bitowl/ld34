package de.bitowl.ld34;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimationDrawable extends TextureRegionDrawable {
    private Animation animation;
    private float stateTime;
    private boolean flipped;


    public AnimationDrawable() {
    }

    public AnimationDrawable(AnimationDrawable drawable) {
        super(drawable);
        setAnimation(drawable.animation);
    }

    public void setFlipped(boolean flip) {
        flipped = flip;
    }
    public boolean isFlipped() {
        return flipped;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        if (animation == null) return;
        batch.draw(animation.getKeyFrame(stateTime), flipped ? x + width : x, y, flipped ? -width : width, height);
    }

    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {

        batch.draw(animation.getKeyFrame(stateTime), flipped ? x + width : x, y, originX, originY, flipped ? -width : width, height, scaleX, scaleY, rotation);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
        stateTime = 0;
        setMinWidth(Math.abs(animation.getKeyFrame(0).getRegionWidth()));
        setMinHeight(Math.abs(animation.getKeyFrame(0).getRegionHeight()));
    }

    public Animation getAnimation() {
        return animation;
    }

    public void reset() {
        stateTime = 0;
    }
}