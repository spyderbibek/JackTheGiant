package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bksapps.jackthegiant.GameMain;

import helpers.GameInfo;
import huds.OptionButtons;

/**
 * Created by TERRORMASTER on 3/11/2018.
 */

public class OptionMenu implements Screen {
    private GameMain game;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;
    private Texture bg;
    private OptionButtons btns;


    public OptionMenu(GameMain game){
        this.game=game;
        mainCamera= new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f, 0);
        gameViewport= new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        bg= new Texture("Backgrounds/Options BG.png");
        btns= new OptionButtons(game);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        game.getBatch().draw(bg, 0,0);
        game.getBatch().end();
        game.getBatch().setProjectionMatrix(btns.getStage().getCamera().combined);
        btns.getStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        bg.dispose();
        btns.getStage().dispose();
    }
}
