package info.simplyapps.game.hedgehog.sprites;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import info.simplyapps.game.hedgehog.rendering.objects.Obstacle;
import info.simplyapps.gameengine.rendering.objects.Animation;
import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.sprites.ViewSprites;

public class GameViewSprites implements ViewSprites {

    public Graphic gBackground;

    public Graphic gButton;
    public Graphic gButtonOverlay;

    public Rect rMsgGameState;
    public Rect rBtnPause;
    public Rect rBtnResume;
    public Rect rBtnBack;
    public Rect rMsgScore;
    public Rect rScore;

    public Rect terrainRect;
    public Point terrainPoint;

    public Graphic[][] gGraphics;

    public Graphic[] gPressButtons;
    public Graphic[] gTerrain;

//	public Graphic[] gWallsH;
//	public Graphic[] gWallsV;

    public List<Obstacle> gObstacle;
    public Animation aHedgehog;
    public Graphic[] gHedgehog;
    public Graphic gBuilding;
    public Animation aBuilding;
    public Graphic gBuilding2;

    public Graphic[] gNight;
    public Graphic[] gHealth;
    public Rect rReady;
    public Rect rGameOver;

    @Override
    public void init() {
    }

    @Override
    public void clean() {
        gBackground = null;
        aHedgehog = null;

        if (gObstacle != null) {
            gObstacle.clear();
        }
        gObstacle = new ArrayList<Obstacle>();
        gHealth = null;
        gNight = null;

        gBuilding = null;
        aBuilding = null;
    }

}
