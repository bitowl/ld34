package de.bitowl.ld34;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Utils {

    public static Texture getDrawable(String name) {
        return new Texture(name + ".png");
    }

    public static BitmapFont font = new BitmapFont();


    public static float B2W = 20f;
    public static float W2B = 1/B2W;


    /**
     * http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=1518
     * @param outline
     * @return
     */
    public static Array<Array<Vector2>> convertShapeOutlineToBox2DFixturesPolygons(Array<Vector2> outline) {

        if (!BayazitDecomposer.IsCounterClockWise(outline)) {
            outline.reverse();
        }

        // outline should be CounterClockWise
        // (although it does gets reversed inside
        // the function anyways...)
        Array<Array<Vector2>> polygons = BayazitDecomposer.ConvexPartition(outline);

        for (int j = 0; j < polygons.size; j++) {
            Array<Vector2> polygon = polygons.get(j);
            if (BayazitDecomposer.IsCounterClockWise(polygon)) {
                polygon.reverse(); // ClockWise Box2D fixtures
            }
        }

        return polygons;
    }


    public static ParticleEffect getParticleEffect(String name) {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particles/"+name+".p"), Gdx.files.internal("."));
        return effect;

    }

    public static Animation getAnimation(float frameDuration, String name, int count, Animation.PlayMode playMode) {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        if (count == 0) {
            regions.add(new TextureRegion(getDrawable(name)));
        }
        for (int i = 1; i <= count ; i++) {
            regions.add(new TextureRegion(getDrawable(name + "_" + i)));
        }
        return new Animation(frameDuration, regions, playMode);
    }


    public static Animation getAnimation(float frameDuration, String name, int[] frames, Animation.PlayMode playMode) {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        for (int i = 0; i < frames.length ; i++) {
            regions.add(new TextureRegion(getDrawable(name + "_" + frames[i])));
        }
        return new Animation(frameDuration, regions, playMode);
    }
}
