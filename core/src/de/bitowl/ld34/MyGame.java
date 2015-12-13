package de.bitowl.ld34;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGame extends Game {

	@Override
	public void create () {
		inst = this;
        setScreen(new MenuScreen());
	}

	private static MyGame inst;
	public static void switchTo(AbstractScreen screen) {
		inst.setScreen(screen);
	}

    @Override
    public void dispose() {
        super.dispose();
        Utils.font.dispose();
        Utils.select.dispose();
        Utils.debugFont.dispose();
    }
}
