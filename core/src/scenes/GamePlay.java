package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bksapps.jackthegiant.GameMain;

import clouds.Cloud;
import clouds.CloudsController;
import helpers.GameInfo;
import helpers.GameManager;
import huds.UIHud;
import player.Player;

/**
 * Created by TERRORMASTER on 3/8/2018.
 */

public class GamePlay implements Screen, ContactListener{
    private GameMain game;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;
    private OrthographicCamera box2DCamera;
    private Box2DDebugRenderer debugRenderer;
    private World world;

    private Sprite [] bgs;
    private float lastYPosition;
    private float cameraSpeed=10;
    private float maxSpeed=10;
    private float acceleration=10;
    private boolean touchedForTheFirstTime=false;
    private CloudsController cloudsController;
    private Player player;
    private UIHud hud;
    private float lastPlayerY;
    private Sound coinSound, lifeSound;

    public GamePlay(GameMain game){
        this.game = game;
    }


    @Override
    public void show() {
        mainCamera= new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f,0);
        gameViewport= new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        box2DCamera= new OrthographicCamera();
        box2DCamera.setToOrtho(false,GameInfo.WIDTH/GameInfo.PPM,
                GameInfo.HEIGHT/GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f,0);
        debugRenderer= new Box2DDebugRenderer();
        hud= new UIHud(game);
        world= new World(new Vector2(0,-9.8f), true);
        world.setContactListener(this);
//        cloud= new Cloud(world, "Cloud 1");
//        cloud.setCloudPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f);
        cloudsController=new CloudsController(world);
        player=cloudsController.positionThePlayer(player);
        createBackgrounds();
        setCameraSpeed();
        coinSound= Gdx.audio.newSound(Gdx.files.internal("Sounds/Coin Sound.wav"));
        lifeSound= Gdx.audio.newSound(Gdx.files.internal("Sounds/Life Sound.wav"));
    }

    public void handleInput(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.movePlayer(-2f);
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.movePlayer(2f);
        }else{
            player.setWalking(false);
        }
    }

    void handleInputAndroid(){
        if(Gdx.input.isTouched()){
            if(Gdx.input.getX()> (GameInfo.WIDTH/2f)){
                player.movePlayer(2f);
            }else {
                player.movePlayer(-2f);
            }
        }else {
            player.setWalking(false);
        }
    }

    void checkForFirstTouch(){
        if(!touchedForTheFirstTime){
            if(Gdx.input.justTouched()){
                touchedForTheFirstTime=true;
                GameManager.getInstance().isPaused=false;
                lastPlayerY=player.getY();
            }
        }
    }

    void update(float dt){
        checkForFirstTouch();
        if(!GameManager.getInstance().isPaused){
            handleInput();
            handleInputAndroid();
            moveCamera(dt);
            checkBackgroundOutOfBounds();
            cloudsController.setCameraY(mainCamera.position.y);
            cloudsController.createAndArrangeNewClouds();
            cloudsController.removeOffScreenCollectables();
            checkPlayerBounds();
            countScore();
        }
    }

    void moveCamera(float delta){

        mainCamera.position.y-=cameraSpeed*delta;

        cameraSpeed+=acceleration*delta;

        if(cameraSpeed>maxSpeed){
            cameraSpeed=maxSpeed;
        }
    }

    void setCameraSpeed(){

        if(GameManager.getInstance().gameData.isEasyDifficulty()){
            cameraSpeed=100;
            maxSpeed=120;
        }
        if(GameManager.getInstance().gameData.isMediumDifficulty()){
            cameraSpeed=120;
            maxSpeed=140;
        }
        if(GameManager.getInstance().gameData.isHardDifficulty()){
            cameraSpeed=140;
            maxSpeed=160;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        drawBackground();
        cloudsController.drawClouds(game.getBatch());
        cloudsController.drawCollectables(game.getBatch());
        player.drawPlayerIdle(game.getBatch());
        player.drawPlayerAnimation(game.getBatch());
        game.getBatch().end();
        //debugRenderer.render(world,box2DCamera.combined);

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();

        game.getBatch().setProjectionMatrix(mainCamera.combined);
        mainCamera.update();

        player.updatePlayer();
        world.step(Gdx.graphics.getDeltaTime(), 6,2);
    }

    void createBackgrounds(){
        bgs= new Sprite[3];
        for(int i=0; i < bgs.length; i++){
            bgs[i]= new Sprite(new Texture("Backgrounds/Game BG.png"));
            bgs[i].setPosition(0, -(i * bgs[i].getHeight()));
            lastYPosition=Math.abs(bgs[i].getY());
        }
    }

    void drawBackground(){
        for(int i=0; i < bgs.length; i++){
            game.getBatch().draw(bgs[i],bgs[i].getX(), bgs[i].getY() );
        }
    }

    void checkBackgroundOutOfBounds(){
        for(int i=0; i < bgs.length; i++){
            if((bgs[i].getY()-bgs[i].getHeight()/2-5)> mainCamera.position.y){
                float newPosition=bgs[i].getHeight()+lastYPosition;
                bgs[i].setPosition(0,-newPosition);
                lastYPosition=Math.abs(newPosition);
            }
        }
    }

    void checkPlayerBounds(){

        if(player.getY()-GameInfo.HEIGHT/2f-player.getHeight()/2f >mainCamera.position.y){
            //Top Out of Bounds
            if(!player.isDead()){
                playerDied();
            }
        }
        if(player.getY()+GameInfo.HEIGHT/2f+player.getHeight()/2f  < mainCamera.position.y){
            //Bottom Out of Bounds
            if(!player.isDead()){
                playerDied();
            }
        }
        if(player.getX()-25> GameInfo.WIDTH || player.getX()+60<0){
            //Right & Left Out of Bounds
            if(!player.isDead()){
                playerDied();
            }
        }

    }

    void countScore(){
        if(lastPlayerY>player.getY()){
            hud.incrementScore(1);
            lastPlayerY=player.getY();
        }
    }

    void playerDied(){
        player.setPosition(1000,1000);
        GameManager.getInstance().isPaused=true;
        //Decrement Life Count
        hud.decrementLife();
        player.setDead(true);

        if(GameManager.getInstance().lifeScore < 0){
            //Player has no more life left to continue
            //Check if new Highscore
            GameManager.getInstance().checkForNewHighscores();
            //Show end score
            hud.createGameOverPanel();

            //wait 3 sec and fade to Main Menu
            RunnableAction run= new RunnableAction();
            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainMenu(game));
                }
            });

            SequenceAction sequenceAction= new SequenceAction();
            sequenceAction.addAction(Actions.delay(3f));
            sequenceAction.addAction(Actions.fadeOut(1f));
            sequenceAction.addAction(run);
            hud.getStage().addAction(sequenceAction);

        }else{

            //Reload the game so player can continue to play
            RunnableAction run= new RunnableAction();
            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new GamePlay(game));
                }
            });

            SequenceAction sequenceAction= new SequenceAction();
            sequenceAction.addAction(Actions.delay(1.5f));
            sequenceAction.addAction(Actions.fadeOut(1f));
            sequenceAction.addAction(run);
            hud.getStage().addAction(sequenceAction);

        }

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
        world.dispose();
        for(int i=0; i<bgs.length; i++){
            bgs[i].getTexture().dispose();
        }
        player.getTexture().dispose();
        debugRenderer.dispose();
        hud.getStage().dispose();
        coinSound.dispose();
        lifeSound.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture body1, body2;

        if(contact.getFixtureA().getUserData()=="Player"){
            body1=contact.getFixtureA();
            body2=contact.getFixtureB();
        }else{
            body1=contact.getFixtureB();
            body2=contact.getFixtureA();
        }

        if(body1.getUserData()=="Player" && body2.getUserData()=="Coin"){
            //collided with the coin
            hud.incrementCoinScore();
            coinSound.play();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();

        }
        if(body1.getUserData()=="Player" && body2.getUserData()=="Life"){
            //Collided with life
            hud.incrementLifeScore();
            lifeSound.play();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();
        }

        if(body1.getUserData()=="Player" && body2.getUserData()=="Dark Cloud"){
            //Collided with Dark Cloud
            if(!player.isDead()){
                playerDied();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
