package de.bitowl.ld34.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;

import de.bitowl.ld34.AnimationDrawable;
import de.bitowl.ld34.GameScreen;
import de.bitowl.ld34.Utils;

public class Player extends Entity {

    float size;

    ParticleEffect bubblesEffect, hurtEffect, collideEffect;

    private AnimationDrawable drawable;

    private Animation base, blink, smile, hurt;

    private float blinkTimer;
    private float BLINK_MIN = 5;
    private float BLINK_MAX = 10;


    public Player() {
        super(Utils.getDrawable("ball_0"));

        drawable = new AnimationDrawable();
        setDrawable(drawable);
        size = 20 * Utils.W2B;


        bubblesEffect = new ParticleEffect();
        bubblesEffect.load(Gdx.files.internal("particles/bubbles.p"), Gdx.files.internal("."));

        hurtEffect = new ParticleEffect();
        hurtEffect.load(Gdx.files.internal("particles/hurt.p"), Gdx.files.internal("."));

        collideEffect = Utils.getParticleEffect("collide");


        // create animations
        base = Utils.getAnimation(1, "ball_0", 0, Animation.PlayMode.LOOP);
        blink = Utils.getAnimation(.03f, "ball", new int[] {1,2,1,0}, Animation.PlayMode.NORMAL);
        smile = Utils.getAnimation(.06f, "ball", new int[] {3,4,4,3,0}, Animation.PlayMode.NORMAL);
        hurt = Utils.getAnimation(.3f, "ball", new int[] {5,0}, Animation.PlayMode.NORMAL);

        drawable.setAnimation(base);


        setWidth(size * 2 * Utils.B2W);
        setHeight(size * 2 * Utils.B2W);
        setOrigin(size * Utils.B2W, size * Utils.B2W);
        // bubblesEffect.setEmittersCleanUpBlendFunction(false);
    }



    public void updateSize(final float newSize) {
        if (newSize <= .4f) {
            System.err.println("=============================");
            System.exit(1);
        }

        addAction(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        setScale(size / newSize, size / newSize);
                        setWidth(newSize * 2 * Utils.B2W);
                        setHeight(newSize * 2 * Utils.B2W);
                        setOrigin(newSize * Utils.B2W, newSize * Utils.B2W);

                        size = newSize;
                    }
                }),
                Actions.scaleTo(1, 1, .5f, Interpolation.exp5Out)
        ));

        
        CircleShape circle = new CircleShape();
        circle.setRadius(newSize);
        getPhysicalObject().changeShape(circle);
       //

        GameScreen.physicRunnables.add(new Runnable() {
            @Override
            public void run() {
                getPhysicalObject().changeMass(newSize);
            }
        });


    }

    @Override
    public void act(float delta) {
        super.act(delta);

        drawable.update(delta);

        int num = (int) (getPhysicalObject().getBody().getLinearVelocity().len2());
        System.out.println("  ->" + num);
        bubblesEffect.findEmitter("bubbles").getEmission().setHigh(2, Math.min(Math.max(num / 5, 2),30));
        bubblesEffect.update(delta);
        hurtEffect.update(delta);
        collideEffect.update(delta);

        // blinking
        blinkTimer += delta;
        if (blinkTimer > BLINK_MIN) {
            if (blinkTimer > BLINK_MAX || MathUtils.random() > .8f) {
                blinkTimer = 0;
                drawable.setAnimation(blink);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        bubblesEffect.draw(batch);
        bubblesEffect.setPosition(getX() + getOriginX(), getY() + getHeight());
        hurtEffect.draw(batch);
        hurtEffect.setPosition(getX() + getOriginX(), getY() + getOriginY());
        collideEffect.draw(batch);
    }

    @Override
    public void collide(Entity userData) {
        if (userData instanceof Drop) {
            Drop drop = (Drop) userData;


            updateSize((float)Math.sqrt(size * size + drop.getSize() *drop.getSize()));
            drawable.setAnimation(smile);
        } else if (userData instanceof Enemy) {
            Enemy enemy = (Enemy) userData;

            hurtEffect.findEmitter("hurt").getSpawnShape().setShape(ParticleEmitter.SpawnShape.square);
            hurtEffect.findEmitter("hurt").getSpawnWidth().setHigh(size  * Utils.B2W);
            hurtEffect.findEmitter("hurt").getSpawnHeight().setHigh(size * Utils.B2W);
            hurtEffect.start();
            updateSize(size - .2f);
            drawable.setAnimation(hurt);
        }
    }

    @Override
    public boolean remove() {
        bubblesEffect.dispose();
        hurtEffect.dispose();
        collideEffect.dispose();
        return super.remove();
    }

    public float getSize() {
        return size;
    }

    /**
     * simple contact for adding particle effects
     * @param contact
     */
    public void simpleContact(Contact contact) {

        Vector2 pos = contact.getWorldManifold().getPoints()[0];
        collideEffect.setPosition(pos.x * Utils.B2W, pos.y * Utils.B2W);
        collideEffect.start();
    }
}
