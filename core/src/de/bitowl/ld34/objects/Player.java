package de.bitowl.ld34.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

    private Sound grow, hurtS, jump, win, lose;


    public Player() {
        super(Utils.getDrawable("ball_0"));

        drawable = new AnimationDrawable();
        setDrawable(drawable);
        size = 20 * Utils.W2B;


        bubblesEffect = Utils.getParticleEffect("bubbles");

        hurtEffect = Utils.getParticleEffect("hurt");

        collideEffect = Utils.getParticleEffect("collide");


        // create animations
        base = Utils.getAnimation(1, "ball_0", 0, Animation.PlayMode.LOOP);
        blink = Utils.getAnimation(.03f, "ball", new int[] {1,2,1,0}, Animation.PlayMode.NORMAL);
        smile = Utils.getAnimation(.3f, "ball", new int[] {4,0}, Animation.PlayMode.NORMAL);
        hurt = Utils.getAnimation(.3f, "ball", new int[] {5,0}, Animation.PlayMode.NORMAL);

        drawable.setAnimation(base);

        // load sounsd
        grow = Gdx.audio.newSound(Gdx.files.internal("sounds/grow.wav"));
        hurtS = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.wav"));
        jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        win = Gdx.audio.newSound(Gdx.files.internal("sounds/win.wav"));
        lose = Gdx.audio.newSound(Gdx.files.internal("sounds/lose.wav"));


        setWidth(size * 2 * Utils.B2W);
        setHeight(size * 2 * Utils.B2W);
        setOrigin(size * Utils.B2W, size * Utils.B2W);
        // bubblesEffect.setEmittersCleanUpBlendFunction(false);

        setScale(0,0);
        addAction(Actions.sequence(Actions.run(new Runnable() {
            @Override
            public void run() {
                GameScreen.cutScene = true; // spawning sequence
            }
        }), Actions.scaleTo(1, 1, 1f, Interpolation.bounceOut), Actions.run(new Runnable() {
            @Override
            public void run() {
                GameScreen.cutScene = false;
            }
        })));
        GameScreen.physicRunnables.add(new Runnable() {
            @Override
            public void run() {
                getPhysicalObject().changeMass(size);
            }
        });
    }



    public void updateSize(final float newSize) {
        if (newSize <= .4f) {

            /// LOOOOSE
            lose.play();
            GameScreen.cutScene = true;
            getStage().getRoot().setOrigin(getX() + getOriginX(), getY() + getOriginY());
            getStage().addAction(Actions.rotateBy(-1200, 3f, Interpolation.pow2In));
            getStage().addAction(Actions.scaleTo(0f, 0f, 3f));
            addAction(Actions.delay(3f, Actions.run(new Runnable() {
                @Override
                public void run() {
                    GameScreen.get().restartLevel();
                }
            })));
            return;
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

        Utils.debugFont.draw(batch, "mass: " + getSize(), getX(), getY());
    }

    @Override
    public void collide(Entity userData) {
        if (userData instanceof Drop) {
            Drop drop = (Drop) userData;


            grow.play();
            updateSize((float) Math.sqrt(size * size + drop.getSize() * drop.getSize()));
            drawable.setAnimation(smile);
        } else if (userData instanceof Enemy) {
            Enemy enemy = (Enemy) userData;

            hurtS.play();
            hurtEffect.findEmitter("hurt").getSpawnShape().setShape(ParticleEmitter.SpawnShape.square);
            hurtEffect.findEmitter("hurt").getSpawnWidth().setHigh(size * Utils.B2W);
            hurtEffect.findEmitter("hurt").getSpawnHeight().setHigh(size * Utils.B2W);
            hurtEffect.start();
            updateSize(size - .2f);
            drawable.setAnimation(hurt);
        } else if (userData instanceof Exit) {
            final Exit exit = (Exit) userData;
            if (exit.isOpen()) {
                // end the level
                GameScreen.cutScene = true;
                win.play();

                getStage().getRoot().setOrigin(exit.getX() + exit.getOriginX(), exit.getY() + exit.getOriginY());
                getStage().addAction(Actions.rotateBy(-1200, 3f, Interpolation.pow2In));
                getStage().addAction(Actions.scaleTo(3f, 3f, 3f));
                addAction(Actions.delay(3f, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GameScreen.get().switchLevel(exit.getNextLevel());
                    }
                })));
                addAction(Actions.moveTo(exit.getX() + exit.getOriginX(), exit.getY() + exit.getOriginY(), .5f));
                addAction(Actions.rotateBy(900, 2f));
                exit.addAction(Actions.rotateBy(-1900, 2f));
                addAction(Actions.scaleTo(0, 0, 2f));

            }
        }
    }

    @Override
    public boolean remove() {
        bubblesEffect.dispose();
        hurtEffect.dispose();
        collideEffect.dispose();
        grow.dispose();
        hurtS.dispose();
        jump.dispose();
        win.dispose();
        lose.dispose();
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
        System.out.println(getPhysicalObject().getBody().getLinearVelocity().dot(contact.getWorldManifold().getNormal()));
        //if (> 6) {
        if (Math.abs(getPhysicalObject().getBody().getLinearVelocity().dot(contact.getWorldManifold().getNormal())) > 4) {
            jump.play();
        }

        Vector2 pos = contact.getWorldManifold().getPoints()[0];
        collideEffect.setPosition(pos.x * Utils.B2W, pos.y * Utils.B2W);
        collideEffect.start();
    }
}
