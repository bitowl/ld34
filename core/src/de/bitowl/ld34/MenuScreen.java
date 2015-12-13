package de.bitowl.ld34;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import javax.xml.soap.Text;


public class MenuScreen extends AbstractScreen {
    private Stage stage;

    private Table table;

    private Table exitDialog;
    private Table levelDialog;

    private Table dialogTable;
    private Image dialogImage;

    public MenuScreen() {
        stage = new Stage(new FillViewport(1280, 1000));


        table = new Table();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(Utils.get9Patch("button_up",29), Utils.get9Patch("button_down",29), null, Utils.font);

        TextButton button = new TextButton("start", style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                MyGame.switchTo(new GameScreen());
            }
        });
        table.add(button).pad(10).width(320).row();

        TextButton load = new TextButton("load level", style);
        load.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                dialogTable.add(levelDialog);
                dialogImage.setVisible(true);
                dialogImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        levelDialog.remove();
                        dialogImage.setVisible(false);

                    }
                });
            }
        });
        table.add(load).pad(10).width(320).row();

        TextButton credits = new TextButton("credits", style);
        credits.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                MyGame.switchTo(new GameScreen("credits"));
            }
        });
        table.add(credits).pad(10).width(320).row();

        final TextButton exit = new TextButton("exit", style);
        table.add(exit).pad(10).width(320).row();
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                dialogTable.add(exitDialog);
                dialogImage.setVisible(true);
                dialogImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        exitDialog.remove();
                        //dialogTable.clearListeners();
                        dialogImage.setVisible(false);

                    }
                });
            }
        });

        Table t = new Table();
        t.setFillParent(true);
        t.add(table);
        stage.addActor(t);

        dialogTable = new Table();
        dialogTable.setFillParent(true);

        dialogImage = new Image(Utils.getDrawable("dark"));
        dialogImage.setFillParent(true);
        dialogImage.setVisible(false);
        stage.addActor(dialogImage);


        stage.addActor(dialogTable);


        // dialogs
        exitDialog = new Table();
        exitDialog.setBackground(Utils.get9Patch("dialog", 29));

        TextButton yes = new TextButton("yes", style);
        yes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                Gdx.app.exit();
            }
        });
        exitDialog.add(yes).pad(10).width(150);

        TextButton no = new TextButton("no", style);
        no.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                exitDialog.remove();
                dialogImage.setVisible(false);
            }
        });
        exitDialog.add(no).pad(10).width(150);

        levelDialog = new Table();
        levelDialog.setBackground(Utils.get9Patch("dialog", 29));

        int LEVEL_COUNT = 4;

        for (int i = 1; i <= LEVEL_COUNT; i++) {
            TextButton lvl = new TextButton(""+i, style);
            final int lvlNr = i;
            lvl.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Utils.select.play();
                    MyGame.switchTo(new GameScreen("lvl" + lvlNr));
                }
            });
            levelDialog.add(lvl).pad(5);
        }

        levelDialog.row();

        TextButton cancel = new TextButton("cancel", style);
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Utils.select.play();
                levelDialog.remove();
                dialogImage.setVisible(false);
            }
        });
        levelDialog.add(cancel).padTop(25).colspan(100);



    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.85f, 0.97f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getViewport().apply();
        stage.getCamera().update();
        table.invalidate();
        table.center();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
