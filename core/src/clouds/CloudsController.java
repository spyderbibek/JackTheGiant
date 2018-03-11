package clouds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import collectables.Collectable;
import helpers.GameInfo;
import player.Player;


/**
 * Created by TERRORMASTER on 3/8/2018.
 */

public class CloudsController {

    private World world;
    private Array<Cloud>clouds= new Array<Cloud>();
    private Array<Collectable> collectables= new Array<Collectable>();
    private final float DISTANCE_BETWEEN_CLOUDS=250f;
    private float minX, maxX;
    private Random random= new Random();
    private float cameraY;
    private float lastCloudPositionY;


    public CloudsController(World world){

        this.world= world;
        minX=GameInfo.WIDTH/2-110;
        maxX=GameInfo.WIDTH/2+110;
        createClouds();
        positionClouds(true);

    }

    void createClouds(){
        for(int i=0; i<2;i++){
            clouds.add(new Cloud(world, "Dark Cloud"));
        }

        int index=1;

        for(int i=0; i<6; i++){
            clouds.add(new Cloud(world, "Cloud "+index));
            index++;
            if(index==4){
                index=1;
            }
        }
        clouds.shuffle();

    }

    public void positionClouds(boolean firstTimeArranging){

        while(clouds.get(0).getCloudName()=="Dark Cloud"){
            clouds.shuffle();
        }

        float positionY= 0;
        int controlX=0;

        if(firstTimeArranging){
            positionY=GameInfo.HEIGHT/2f;
        }else{
            positionY=lastCloudPositionY;
        }

        for(Cloud cloud: clouds){
            if(cloud.getX()==0 && cloud.getY()==0){
                float tempX= 0;

                if(controlX==0){
                    tempX= randomBetweenNumbers(maxX-40, maxX);
                    controlX=1;
                    cloud.setDrawLeft(false);
                }else  if(controlX==1){
                    tempX=randomBetweenNumbers(minX+40,minX);
                    controlX=0;
                    cloud.setDrawLeft(true);
                }

                cloud.setCloudPosition(tempX, positionY);
                positionY-=DISTANCE_BETWEEN_CLOUDS;
                lastCloudPositionY=positionY;
            }
        }

        //Remove this later. This is just a test
        Collectable c1= new Collectable(world, "Life");
        c1.setCollectablePosition(clouds.get(1).getX(), clouds.get(1).getY()+40);
        collectables.add(c1);

    }

    private float randomBetweenNumbers(float min, float max){
        return  random.nextFloat()*(max-min)+min;
    }


    public void drawClouds(SpriteBatch batch){
        for(Cloud cloud: clouds){
            if(cloud.getDrawLeft()){
                batch.draw(cloud, cloud.getX()-cloud.getWidth()/2f-20, cloud.getY()-cloud.getHeight()/2f);
            }else{
                batch.draw(cloud, cloud.getX()-cloud.getWidth()/2f+10, cloud.getY()-cloud.getHeight()/2f);
            }
        }
    }

    public void drawCollectables(SpriteBatch batch){
        for(Collectable collectable : collectables){
            collectable.updateCollectable();
            batch.draw(collectable, collectable.getX(), collectable.getY());
        }
    }

    public void createAndArrangeNewClouds(){

        for(int i=0; i<clouds.size; i++){
            if((clouds.get(i).getY()-GameInfo.HEIGHT/2-10)> cameraY){
                clouds.get(i).getTexture().dispose();
                clouds.removeIndex(i);
            }
        }

        if(clouds.size==4){
            createClouds();
            positionClouds(false);
        }

    }

    public void setCameraY(float cameraY){
        this.cameraY=cameraY;
    }

    public Player positionThePlayer(Player player){
        player=new Player(world, clouds.get(0).getX(), clouds.get(0).getY()+78);
        return  player;
    }


}//CloudsController
