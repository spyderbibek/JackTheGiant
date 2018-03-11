package huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bksapps.jackthegiant.GameMain;

import helpers.GameInfo;
import scenes.MainMenu;

/**
 * Created by TERRORMASTER on 3/11/2018.
 */

public class OptionButtons {
    private GameMain game;
    private Viewport gameViewport;
    private Stage stage;
    private ImageButton easyBtn, mediumBtn, hardBtn, backBtn;
    private Image sign;


    public OptionButtons(GameMain game){
        this.game=game;
        gameViewport= new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());
        stage= new Stage(gameViewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);
        createAndPositionButtons();
        addAllListeners();
        stage.addActor(easyBtn);
        stage.addActor(mediumBtn);
        stage.addActor(hardBtn);
        stage.addActor(backBtn);
        stage.addActor(sign);
    }

    void createAndPositionButtons(){

        easyBtn= new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Menu/Easy.png"))));
        mediumBtn= new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Menu/Medium.png"))));
        hardBtn= new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Menu/Hard.png"))));
        backBtn= new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Menu/Back.png"))));

        sign= new Image(new Texture("Buttons/Options Menu/Check Sign.png"));

        backBtn.setPosition(17,17,Align.bottomLeft);
        easyBtn.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2+40, Align.center);
        mediumBtn.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2-40, Align.center);
        hardBtn.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2-120, Align.center);

        //Remove later
        sign.setPosition(GameInfo.WIDTH/2+76, mediumBtn.getY()+13, Align.bottomLeft);

    }

    void addAllListeners(){
        easyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sign.setY(easyBtn.getY()+13);
            }
        });
        mediumBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sign.setY(mediumBtn.getY()+13);
            }
        });
        hardBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sign.setY(hardBtn.getY()+13);
            }
        });
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
            }
        });
    }

    public Stage getStage() {
        return stage;
    }
}