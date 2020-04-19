
package info.simplyapps.game.hedgehog.rendering;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.game.hedgehog.Constants;
import info.simplyapps.game.hedgehog.R;
import info.simplyapps.game.hedgehog.SystemHelper;
import info.simplyapps.game.hedgehog.engine.GameValues;
import info.simplyapps.game.hedgehog.engine.HedgehogEngine;
import info.simplyapps.game.hedgehog.engine.HedgehogGameMap;
import info.simplyapps.game.hedgehog.rendering.objects.Obstacle;
import info.simplyapps.game.hedgehog.sprites.GameViewSprites;
import info.simplyapps.game.hedgehog.storage.DBDriver;
import info.simplyapps.game.hedgehog.storage.dto.CurrentGame;
import info.simplyapps.game.hedgehog.system.HedgehogGame;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.pathfinding.AStarPathFinder;
import info.simplyapps.gameengine.pathfinding.Path;
import info.simplyapps.gameengine.pathfinding.Step;
import info.simplyapps.gameengine.pathfinding.heuristics.ManhattanHeuristic;
import info.simplyapps.gameengine.pathfinding.impl.UnitMover;
import info.simplyapps.gameengine.pathfinding.interfaces.PathFinder;
import info.simplyapps.gameengine.rendering.kits.AnimationKit;
import info.simplyapps.gameengine.rendering.kits.Renderkit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit.ScreenPosition;
import info.simplyapps.gameengine.rendering.objects.Animation;
import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.system.BasicGame;
import info.simplyapps.gameengine.system.GameRound;
import info.simplyapps.gameengine.system.GameState;
import info.simplyapps.gameengine.system.GameSubState;

public class GameRenderer extends HedgehogRendererTemplate implements HedgehogEngine {

    /**
     * The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN
     */
    private GameState mMode;
    private GameSubState mSubMode;

    private HedgehogGame mGame;

    Random rnd;

    private long delay = 0l;
    private long lastTime;

    final float standardNumberWidth = 0.05f;
    final float standardNumberHeight = 1.20f;

    // UPDATE VALUES

    private int mHedgehogWidth;
    private int mHedgehogHeight;
    private HedgehogGameMap mGameMap;

    // Pathfinding
    private boolean creating;

    int tileWidth;
    int tileHeight;

    public static int gameMapWidth;
    public static int gameMapHeight;

    Paint pTerrain;

    float rotation = 10f;

    private boolean isMedium;
    private boolean isHard;


    public GameRenderer(Context context, Properties p) {
        super(context, p);
    }

    @Override
    public void doInitThread(long time) {
        rnd = new Random();

        creating = false;

        sprites = new GameViewSprites();
        mMode = GameState.NONE;
        mSubMode = GameSubState.NONE;

        Configuration cDifficulty = SystemHelper.getConfiguration(EngineConstants.CONFIG_DIFFICULTY, EngineConstants.DEFAULT_CONFIG_DIFFICULTY);
        isMedium = Integer.valueOf(cDifficulty.value) == GameValues.DIFFICULTY_MEDIUM;
        isHard = Integer.valueOf(cDifficulty.value) == GameValues.DIFFICULTY_HARD;

        if (mGame == null) {
            createGame();
        }

        // create background
        if (mGame.hasGame()) {
//            updateRoundGraphic();
        }

        // load background for level choosing
//        getSprites().gBackground = loadGraphic(R.drawable.background);

        getSprites().rMsgGameState = new Rect(0, 0, screenWidth, screenHeight);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.5f, 0, 0, getSprites().rMsgGameState);
        getSprites().rMsgGameState.bottom -= getSprites().rMsgGameState.height() / 3;

        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_flat, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white, 0, 0);

        getSprites().rBtnPause = getSprites().gButton.image.copyBounds();
        getSprites().rBtnResume = getSprites().gButton.image.copyBounds();
        getSprites().rBtnBack = getSprites().gButton.image.copyBounds();

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.15f, 15, 60, getSprites().rBtnPause);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.15f, 15, 60, getSprites().rBtnResume);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 50, 60, getSprites().rBtnBack);

        // Create text
        getSprites().rReady = getSprites().gButton.image.copyBounds();
        getSprites().rGameOver = getSprites().gButton.image.copyBounds();

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.25f, 0, 0, getSprites().rReady);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.25f, 0, 0, getSprites().rGameOver);

        getSprites().rMsgScore = getSprites().gButton.image.copyBounds();
        getSprites().rScore = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.20f, 150, 100, getSprites().rMsgScore);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.20f, 50, 100, getSprites().rScore);

        gameMapWidth = (Integer) super.gameProperties.get(Constants.GameProperties.GAMEMAP_W);
        gameMapHeight = (Integer) super.gameProperties.get(Constants.GameProperties.GAMEMAP_H);
        tileWidth = screenWidth / gameMapWidth;
        tileHeight = tileWidth;//screenHeight / gameMapHeight;

        // calculate tiles on game height
//        if(Math.floor(screenHeight / tileHeight) * tileHeight < screenHeight) {
//        	gameMapHeight = (screenHeight / tileHeight);
//        	// crunch tile height
//        	tileHeight = screenHeight / gameMapHeight;
//        }

        initGamingArea();
    }

    private int getRoundTime() {
        if (isHard) {
            return GameValues.roundTimeHard;
        } else if (isMedium) {
            return GameValues.roundTimeMedium;
        }
        return GameValues.roundTime;
    }

    private void createGame() {
        mGame = new HedgehogGame(new GameRound[]{
                new GameRound(0, getRoundTime(), -1)
        });
        mGame.life = GameValues.totalLife;
        mGame.points = 0;
    }

    /**
     * Create the play area
     * Do this only once per game
     */
    private void initGamingArea() {

        mHedgehogWidth = tileWidth;
        mHedgehogHeight = tileHeight;

        pTerrain = new Paint();
        pTerrain.setColor(GameValues.greenPoolColor);
        pTerrain.setStyle(Paint.Style.FILL);

        getSprites().gHedgehog = new Graphic[2];
        getSprites().gHedgehog[0] = loadGraphic(R.drawable.hedgehog_0, 0, 0);
        getSprites().gHedgehog[1] = loadGraphic(R.drawable.hedgehog_1, 0, 0);

        getSprites().aHedgehog = new Animation();
        AnimationKit.addAnimation(getSprites().aHedgehog, 0, 250);
        AnimationKit.addAnimation(getSprites().aHedgehog, 1, 250);
        getSprites().aHedgehog.rect = new Rect(0, 0, mHedgehogWidth, mHedgehogHeight);

        getSprites().gBuilding = loadGraphic(R.drawable.building);
        getSprites().gBuilding2 = loadGraphic(R.drawable.build_0);
        getSprites().aBuilding = new Animation();
        AnimationKit.addAnimation(getSprites().aBuilding, 0, 250);

        getSprites().gPressButtons = new Graphic[4];
        getSprites().gPressButtons[0] = loadGraphic(R.drawable.pressbuttons, 0, 0);
        getSprites().gPressButtons[1] = loadGraphic(R.drawable.pressbuttons1, 0, 0);
        getSprites().gPressButtons[2] = loadGraphic(R.drawable.pressbuttons2, 0, 0);
        getSprites().gPressButtons[3] = loadGraphic(R.drawable.pressbuttons3, 0, 0);
        // left
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.1f, 10, 200, getSprites().gPressButtons[0]);
        // top
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.1f, 50, 40, getSprites().gPressButtons[1]);
        // right
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.1f, 90, 200, getSprites().gPressButtons[2]);
        // bottom
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.1f, 50, 370, getSprites().gPressButtons[3]);

//      TypedArray walls = mContext.getResources().obtainTypedArray(R.array.level_water_tiles);
//      TypedArray trees = mContext.getResources().obtainTypedArray(R.array.level_tree_tiles);
        TypedArray foods = mContext.getResources().obtainTypedArray(R.array.level_food_tiles);
        TypedArray hunter = mContext.getResources().obtainTypedArray(R.array.level_hunter_tiles);
        TypedArray tunnel = mContext.getResources().obtainTypedArray(R.array.level_tunnel_tiles);

        getSprites().gGraphics = new Graphic[5][3];
        for (int i = 0; i < getSprites().gGraphics.length; i++) {
            switch (i) {
                case GameValues.DRAW_FOOD:
                    getSprites().gGraphics[i][0] = new Graphic(foods.getDrawable(0));
                    getSprites().gGraphics[i][1] = new Graphic(foods.getDrawable(1));
                    getSprites().gGraphics[i][2] = new Graphic(foods.getDrawable(2));
                    break;
                case GameValues.DRAW_HUNTER:
                    getSprites().gGraphics[i][0] = new Graphic(hunter.getDrawable(0));
                    break;
//          case GameValues.DRAW_TRAP:
//              getSprites().gGraphics[i][0] = new Graphic(trees.getDrawable(0));
//              break;
                case GameValues.DRAW_TUNNEL:
                    getSprites().gGraphics[i][0] = new Graphic(tunnel.getDrawable(0));
                    getSprites().gGraphics[i][1] = new Graphic(tunnel.getDrawable(1));
                    break;
//          case GameValues.DRAW_WALL:
//              getSprites().gGraphics[i][0] = new Graphic(walls.getDrawable(0));
//              break;
            }
        }

        getSprites().gTerrain = new Graphic[11];
        getSprites().gTerrain[0] = loadButtonGraphic(R.drawable.map_grass, EngineConstants.PathFinding.TERRAIN_GRASS);
        getSprites().gTerrain[1] = loadButtonGraphic(R.drawable.map_water, EngineConstants.PathFinding.TERRAIN_WATER);
        getSprites().gTerrain[2] = loadButtonGraphic(R.drawable.map_forest, EngineConstants.PathFinding.TERRAIN_FOREST);
        getSprites().gTerrain[3] = loadButtonGraphic(R.drawable.map_desert, EngineConstants.PathFinding.TERRAIN_DESERT);
        getSprites().gTerrain[4] = loadButtonGraphic(R.drawable.map_stones, EngineConstants.PathFinding.TERRAIN_STONES);
        getSprites().gTerrain[5] = loadButtonGraphic(R.drawable.obj_palm, EngineConstants.PathFinding.STATICS_PALM);
        getSprites().gTerrain[6] = loadButtonGraphic(R.drawable.obj_water_h0, EngineConstants.PathFinding.STATICS_WATEREND);
        getSprites().gTerrain[7] = loadButtonGraphic(R.drawable.obj_water_h1, EngineConstants.PathFinding.STATICS_WATERMIDDLE);
        getSprites().gTerrain[8] = loadButtonGraphic(R.drawable.obj_forest, EngineConstants.PathFinding.STATICS_FOREST);
        getSprites().gTerrain[9] = loadButtonGraphic(R.drawable.obj_pool, EngineConstants.PathFinding.STATICS_POOL);
        getSprites().gTerrain[10] = loadButtonGraphic(R.drawable.obj_stones, EngineConstants.PathFinding.STATICS_STONES);

        getSprites().terrainRect = new Rect(0, 0, tileWidth, tileHeight);
        getSprites().terrainPoint = new Point(0, 0);

        // create food objects
        getSprites().gObstacle = new ArrayList<>();
    }

    public synchronized BasicGame getGame() {
        return mGame;
    }

    /**
     * Starts the game, setting parameters for the current difficulty.
     */
    public void doStart() {
        if (mMode == GameState.NONE) {
            setMode(GameState.INIT);
        }
    }

    /**
     * Pauses the physics update & animation.
     */
    public synchronized void pause() {
        saveGameState();
        setSubMode(GameSubState.PAUSE);
    }

    /**
     * Resumes from a pause.
     */
    public synchronized void unpause() {
        //set state back to running
        lastTime = System.currentTimeMillis();
        setSubMode(GameSubState.NONE);
    }

    public synchronized void exit() {
        super.exit();
        getSprites().clean();
        CurrentGame cg = SystemHelper.getCurrentGame();
        cg.life = 0.0f;
        DBDriver.getInstance().store(cg);
    }

    public synchronized void create() {
        delay = GameValues.gameRoundDelay;
        super.create();
    }

    public synchronized void restoreGameState() {
        log("restoreGameState()");

        CurrentGame cg = SystemHelper.getCurrentGame();
        if (cg.life > 0.0f) {

            mGame.currentRound = cg.round;
            mGame.life = cg.life;
            mGame.points = cg.points;
            // safety check if no points and lost life
            if (mGame.currentRound == 0 && mGame.life == 0 && mGame.points == 0) {
                mGame.life = 1.0f;
            }
            if (mGame.getCurrentRound() != null) {
                mGame.getCurrentRound().time = cg.time;
            }
            setMode(GameState.READY);
        }

        if (mGame.hasGame()) {
            updateRoundGraphic();
        }
    }

    public synchronized void saveGameState() {
        log("saveGameState()");
        CurrentGame cg = SystemHelper.getCurrentGame();

        final boolean finished = mGame.finished();
        if (!finished) {
            cg.round = mGame.currentRound;
            cg.life = mGame.life;
            cg.points = mGame.points;
            cg.time = mGame.getCurrentRound().time;
        } else {
            // reset after won
            cg.round = 0;
            cg.points = 0;
            cg.coins = 0;
            cg.life = 1.0f;
            cg.time = getRoundTime();
        }

        DBDriver.getInstance().store(cg);
    }

    /**
     * Restores game state from the indicated Bundle. Typically called when the
     * Activity is being restored after having been previously destroyed.
     *
     * @param savedState Bundle containing the game state
     */
    public synchronized void restoreState(Bundle savedState) {
        setMode(GameState.INIT);
        restoreGameState();
    }

    /**
     * Dump game state to the provided Bundle. Typically called when the
     * Activity is being suspended.
     *
     * @return Bundle with this view's state
     */
    public Bundle saveState(Bundle map) {
        if (map != null) {
            saveGameState();
        }
        return map;
    }

    @Override
    public void doUpdateRenderState() {
        final long time = System.currentTimeMillis();

        if (delay > 0L && lastTime > 0L) {
            delay -= time - lastTime;
        }


        switch (mMode) {
            case NONE: {
                // move to initialization
                setMode(GameState.INIT);
            }
            break;
            case INIT: {

                if (mGame.currentRound < 0) {
                    mGame.currentRound = 0;
                }

                updateRoundGraphic();

                // setup the game
                setMode(GameState.READY);

            }
            break;
            case READY: {
                if (!creating) {
                    setMode(GameState.PLAY);
                }
            }
            break;
            case PLAY: {
                // active gameplay
                // calculate game time
                if (delay <= 0L && lastTime > 0L) {
                    if (mSubMode == GameSubState.NONE) {
                        mGame.getCurrentRound().time -= time - lastTime;
                    }
                }

                // update graphic positions
                updatePhysics();

                // end game
                if (mGame.getCurrentRound().time < 0) {
                    setMode(GameState.END);
                }
                if (mGame.life <= 0.0f) {
                    setMode(GameState.END);
                    mGame.complete = true;
                    mGame.lifeLost = true;
                }

            }
            break;
            case END: {
                if (mGame.finished() && mGame.complete) {
                    if (mGame.lifeLost) {
                        // update once
                        updateStatistics();
                        mGame.reset();
                    }
                } else {
                    // execute once - initialize new round
                    setMode(GameState.INIT);
                    mGame.addRound(new GameRound(0, getRoundTime(), -1));
                    mGame.currentRound += 1;
                    delay = GameValues.gameRoundDelay;
                    mGame.getCurrentRound().fpsdelay = 0;
                }

            }
            break;
            default:
                setMode(GameState.NONE);
                break;
        }

        lastTime = time;
    }

    private void updateRoundGraphic() {
        if (!creating) {
            getSprites().gObstacle.clear();

            // create level
            new BuildTask().execute();
        }
    }

    private void updateStatistics() {
        log("updateStatistics()");
        SystemHelper.getInventory().gamesWon += 1;
        // award 12.5 percent of the score as coins
        SystemHelper.getInventory().coins += mGame.points / GameValues.scoreDividerSinglePlayer;
        SystemHelper.getInventory().points += mGame.points;

        List<Integer> list = new ArrayList<>();
        list.add(SystemHelper.getInventory().game1);
        list.add(SystemHelper.getInventory().game2);
        list.add(SystemHelper.getInventory().game3);
        list.add(SystemHelper.getInventory().game4);
        list.add(SystemHelper.getInventory().game5);
        list.add(mGame.points);
        Collections.sort(list);
        SystemHelper.getInventory().game1 = list.get(5);
        SystemHelper.getInventory().game2 = list.get(4);
        SystemHelper.getInventory().game3 = list.get(3);
        SystemHelper.getInventory().game4 = list.get(2);
        SystemHelper.getInventory().game5 = list.get(1);

        DBDriver.getInstance().store(SystemHelper.getInventory());
    }

    /**
     * Used to signal the thread whether it should be running or not. Passing
     * true allows the thread to run; passing false will shut it down if it's
     * already running. Calling start() after this was most recently called with
     * false will result in an immediate shutdown.
     *
     * @param b true to run, false to shut down
     */
    public void setRunning(boolean b) {
        super.setRunning(b);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean blocked = false;

        if (mMode == GameState.PLAY) {
            if (mSubMode == GameSubState.NONE) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        int[] unit = null;
                        boolean move = false;
                        if (containsClick(getSprites().gPressButtons[0].image.getBounds(), event.getX(), event.getY())) {
                            unit = mGameMap.getHeroUnitPosition();
                            unit[0] -= 1;
                            if (unit[0] < 0) {
                                unit[0] = 0;
                            }
                            move = true;
                        }
                        if (containsClick(getSprites().gPressButtons[1].image.getBounds(), event.getX(), event.getY())) {
                            unit = mGameMap.getHeroUnitPosition();
                            unit[1] -= 1;
                            if (unit[1] < 0) {
                                unit[1] = 0;
                            }
                            move = true;
                        }
                        if (containsClick(getSprites().gPressButtons[2].image.getBounds(), event.getX(), event.getY())) {
                            unit = mGameMap.getHeroUnitPosition();
                            unit[0] += 1;
                            if (unit[0] >= GameValues.GAMETILES_X) {
                                unit[0] = GameValues.GAMETILES_X - 1;
                            }
                            move = true;
                        }
                        if (containsClick(getSprites().gPressButtons[3].image.getBounds(), event.getX(), event.getY())) {
                            unit = mGameMap.getHeroUnitPosition();
                            unit[1] += 1;
                            if (unit[1] >= GameValues.GAMETILES_Y) {
                                unit[1] = GameValues.GAMETILES_Y - 1;
                            }
                            move = true;
                        }
                        if (move) {
                            if (!mGameMap.blocked(mGame.mover, unit[0], unit[1])) {
                                mGameMap.moveHeroUnitPosition(unit[0], unit[1]);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (containsClick(getSprites().rBtnPause, event.getX(), event.getY())) {
                            setSubMode(GameSubState.PAUSE);
                            blocked = true;
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // move
                        break;
                }
            }
        }

        if (mMode == GameState.END && mGame.finished()) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }
            }
        }

        if (!blocked && mSubMode == GameSubState.PAUSE) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (containsClick(getSprites().rBtnResume, event.getX(), event.getY())) {
                        setSubMode(GameSubState.NONE);
                    }
                    if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }
                    break;
            }
        }

        return true;
    }

    /**
     * Update the graphic x/y values in real time. This is called before the
     * draw() method
     */
    private void updatePhysics() {

        // the fixed time for drawing this frame
        final long time = System.currentTimeMillis();

        if (mMode == GameState.PLAY) {

            if (mSubMode == GameSubState.NONE) {

                getSprites().aHedgehog.nextFrame(time);

                // prepare post-movement checks
                if (getSprites().gObstacle != null) {
                    int[] tunnelPos = null;
                    List<Obstacle> removals = new ArrayList<Obstacle>();

                    // check movement on obstacles
                    boolean hasFood = false;
                    boolean inTunnel = false;
                    int[] unit = mGameMap.getHeroUnitPosition();
                    for (Obstacle o : getSprites().gObstacle) {
                        if (EngineConstants.PathFinding.OBJECT_FOOD == o.type) {
                            hasFood = true;
                            if (!o.hit && unit[0] == o.x && unit[1] == o.y) {
                                // catch food
                                if (isMedium) {
                                    mGame.points += (o.points * 2);
                                } else if (isHard) {
                                    mGame.points += (o.points * 3);
                                } else {
                                    mGame.points += o.points;
                                }
                                o.hit = true;
                                removals.add(o);
                            }
                        } else if (EngineConstants.PathFinding.UNIT_HUNTER == o.type) {
                            if (unit[0] == o.x && unit[1] == o.y) {
                                mGame.life = 0;
                            }
                        } else if (EngineConstants.PathFinding.OBJECT_TUNNEL == o.type
                                && unit[0] == o.x && unit[1] == o.y) {
                            inTunnel = true;
                            // check tunnel warping
                            if (!mGame.inTunnel) {
                                // warp to other tunnel
                                for (Obstacle o1 : getSprites().gObstacle) {
                                    if (EngineConstants.PathFinding.OBJECT_TUNNEL == o1.type
                                            && unit[0] != o1.x && unit[1] != o1.y
                                            && o1.x != o.x && o1.y != o.y) {
                                        tunnelPos = new int[2];
                                        tunnelPos[0] = o1.x;
                                        tunnelPos[1] = o1.y;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    mGame.inTunnel = inTunnel;
                    if (!hasFood) {
                        setMode(GameState.END);
                    }
                    // warp hedgehog to other tunnel position
                    if (tunnelPos != null) {
                        mGameMap.moveHeroUnitPosition(tunnelPos[0], tunnelPos[1]);
                    }

                    long delay = GameValues.hunterDelay;
                    if (isMedium) {
                        delay = Float.valueOf(Long.valueOf(delay).floatValue() * GameValues.hunterSpeedMedium).longValue();
                    } else if (isHard) {
                        delay = Float.valueOf(Long.valueOf(delay).floatValue() * GameValues.hunterSpeedHard).longValue();
                    }
                    // move the hunters
                    for (Obstacle o : getSprites().gObstacle) {
                        if (EngineConstants.PathFinding.UNIT_HUNTER == o.type) {
                            if (o.lastMove + delay < time) {
                                int tX = o.x;
                                int tY = o.y;
                                // determine hunter movement
                                o.lastMove = time;
                                int leftMod = 1;
                                int topMod = 1;
                                if (unit[0] < o.x) {
                                    leftMod = -1;
                                }
                                if (unit[0] == o.x) {
                                    leftMod = 0;
                                }
                                tX += leftMod;
                                // prepare movement checks
                                if (!mGameMap.blocked(o.type, tX, tY)) {
                                    mGameMap.moveUnitPosition(o.type, o.x, o.y, tX, tY);
                                    o.x = tX;
                                    o.y = tY;
                                }

                                if (unit[1] < o.y) {
                                    topMod = -1;
                                }
                                if (unit[1] == o.y) {
                                    topMod = 0;
                                }
                                tY += topMod;
                                // prepare movement checks
                                if (!mGameMap.blocked(o.type, tX, tY)) {
                                    mGameMap.moveUnitPosition(o.type, o.x, o.y, tX, tY);
                                    o.x = tX;
                                    o.y = tY;
                                }
                            }
                        }
                    }

                    // remove obstacle
                    if (removals.size() > 0) {
                        getSprites().gObstacle.removeAll(removals);
                        removals.clear();
                    }
                }

            }

        }
    }

    /**
     * Draws the graphics onto the Canvas.
     */
    @Override
    public void doDrawRenderer(Canvas canvas) {

        // the fixed time for drawing this frame
        final long time = System.currentTimeMillis();

        canvas.drawRect(0, 0, screenWidth, gameMapHeight * tileHeight, pTerrain);

        if (mMode == GameState.PLAY) {

            if (mGame.hasGame() && mGame.getCurrentRound() != null) {

                // generate viewpane
                int[] unit = mGameMap.getHeroUnitPosition();

                // create unit as center of the screen
                int centerX = unit[0];
                int centerY = unit[1];
                // check center for available border spacing - gameMap[Width|Height] / 2

                int borderLR = gameMapWidth / 2;
                int borderTB = gameMapHeight / 2;

                // map - 32x32
                // screen - 12x8 - half 6x4
                // center - 5x10
                // alignment = -1x6
                // center2 - 6x10

                // align left side
                if (centerX - borderLR < 0) {
                    centerX = centerX - (centerX - borderLR);
                }
                // align top side
                if (centerY - borderTB < 0) {
                    centerY = centerY - (centerY - borderTB);
                }
                // align right side
                if (centerX + borderLR >= GameValues.GAMETILES_X) {
                    centerX = GameValues.GAMETILES_X - borderLR;
                }
                // align bottom side
                if (centerY + borderTB >= GameValues.GAMETILES_Y) {
                    centerY = GameValues.GAMETILES_Y - borderTB - 1;
                }

                int left = centerX - borderLR;
                int top = centerY - borderTB;

                // draw terrain
                for (int cX = 0, x = left; x < left + gameMapWidth; x++, cX++) {
                    for (int cY = 0, y = top; y < top + gameMapHeight; y++, cY++) {
                        int i = 0;
                        int terrain = mGameMap.getTerrain(x, y);
                        int translated = 0;
                        for (Graphic g : getSprites().gTerrain) {
                            if (g.clickAction == terrain) {
                                translated = i;
                                break;
                            }
                            i++;
                        }
                        float rotation = mGameMap.getRotation(x, y);
                        getSprites().terrainPoint.x = cX * tileWidth;
                        getSprites().terrainPoint.y = cY * tileHeight;
                        canvas.save();
                        getSprites().terrainRect.offsetTo(getSprites().terrainPoint.x, getSprites().terrainPoint.y);
                        getSprites().gTerrain[translated].image.setBounds(getSprites().terrainRect);
                        if (rotation > 0.0f) {
                            canvas.rotate(rotation, Float.valueOf(getSprites().gTerrain[translated].image.getBounds().centerX()), Float.valueOf(getSprites().gTerrain[translated].image.getBounds().centerY()));
                            getSprites().terrainRect.offsetTo(getSprites().terrainPoint.x, getSprites().terrainPoint.y);
                            getSprites().gTerrain[translated].image.setBounds(getSprites().terrainRect);
                        }
                        getSprites().gTerrain[translated].image.draw(canvas);
                        // DEBUG OUTPUT : field position
//                      canvas.drawText(Integer.toString(x) + "x" + Integer.toString(y), sprites.terrainRect.left, sprites.terrainRect.bottom, pDebug);
                        canvas.restore();
                    }
                }
                // draw statics
                for (int cX = 0, x = left; x < left + gameMapWidth; x++, cX++) {
                    for (int cY = 0, y = top; y < top + gameMapHeight; y++, cY++) {
                        int i = 0;
                        int terrain = mGameMap.getStatics(x, y);
                        if (terrain <= 0) {
                            continue;
                        }
                        int translated = 0;
                        for (Graphic g : getSprites().gTerrain) {
                            if (g.clickAction == terrain) {
                                translated = i;
                                break;
                            }
                            i++;
                        }
                        float rotation = mGameMap.getRotationStatics(x, y);
                        getSprites().terrainPoint.x = cX * tileWidth;
                        getSprites().terrainPoint.y = cY * tileHeight;
                        canvas.save();
                        getSprites().terrainRect.offsetTo(getSprites().terrainPoint.x, getSprites().terrainPoint.y);
                        getSprites().gTerrain[translated].image.setBounds(getSprites().terrainRect);
                        if (rotation > 0.0f) {
                            canvas.rotate(rotation, Float.valueOf(getSprites().gTerrain[translated].image.getBounds().centerX()), Float.valueOf(getSprites().gTerrain[translated].image.getBounds().centerY()));
                            getSprites().terrainRect.offsetTo(getSprites().terrainPoint.x, getSprites().terrainPoint.y);
                            getSprites().gTerrain[translated].image.setBounds(getSprites().terrainRect);
                        }
                        getSprites().gTerrain[translated].image.draw(canvas);
                        canvas.restore();
                    }
                }
                // draw semi statics
                for (int cX = 0, x = left; x < left + gameMapWidth; x++, cX++) {
                    for (int cY = 0, y = top; y < top + gameMapHeight; y++, cY++) {
                        int i = 0;
                        int terrain = mGameMap.getSemiStatics(x, y);
                        if (terrain <= 0) {
                            continue;
                        }
                        int translated = 0;
                        for (Graphic g : getSprites().gTerrain) {
                            if (g.clickAction == terrain) {
                                translated = i;
                                break;
                            }
                            i++;
                        }
                        getSprites().terrainPoint.x = cX * tileWidth;
                        getSprites().terrainPoint.y = cY * tileHeight;
                        canvas.save();
                        getSprites().terrainRect.offsetTo(getSprites().terrainPoint.x, getSprites().terrainPoint.y);
                        getSprites().gTerrain[translated].image.setBounds(getSprites().terrainRect);
                        getSprites().gTerrain[translated].image.draw(canvas);
                        canvas.restore();
                    }
                }

                // draw the obstacles
                for (Obstacle o : getSprites().gObstacle) {
                    if (o.x >= left && o.x < left + gameMapWidth
                            && o.y >= top && o.y < top + gameMapHeight) {
                        getSprites().terrainPoint.x = (o.x >= left ? o.x - left : o.x) * tileWidth;
                        getSprites().terrainPoint.y = (o.y >= top ? o.y - top : o.y) * tileHeight;
                        canvas.save();
                        getSprites().terrainRect.offsetTo(getSprites().terrainPoint.x, getSprites().terrainPoint.y);
                        int translatedType = o.type;
                        switch (o.type) {
                            case EngineConstants.PathFinding.OBJECT_FOOD:
                                translatedType = GameValues.DRAW_FOOD;
                                break;
                            case EngineConstants.PathFinding.OBJECT_TUNNEL:
                                translatedType = GameValues.DRAW_TUNNEL;
                                break;
                            case EngineConstants.PathFinding.UNIT_HUNTER:
                                translatedType = GameValues.DRAW_HUNTER;
                                break;
                        }
                        getSprites().gGraphics[translatedType][o.gReference].image.setBounds(getSprites().terrainRect);
                        getSprites().gGraphics[translatedType][o.gReference].image.draw(canvas);
                        canvas.restore();
                    }
                }


                // translate tile into pixel
                int x = (unit[0] > borderLR ? unit[0] - left : unit[0]) * tileWidth;
                int y = (unit[1] > borderTB ? unit[1] - top : unit[1]) * tileHeight;
                getSprites().aHedgehog.nextFrame();
                getSprites().aHedgehog.rect.offsetTo(x, y);
                getSprites().gHedgehog[getSprites().aHedgehog.nextFrame().gReference].image.setBounds(getSprites().aHedgehog.rect);
                getSprites().gHedgehog[getSprites().aHedgehog.nextFrame().gReference].image.draw(canvas);

                // draw buttons on top of all
                if (getSprites().gPressButtons != null) {
                    getSprites().gPressButtons[0].image.draw(canvas);
                    getSprites().gPressButtons[1].image.draw(canvas);
                    getSprites().gPressButtons[2].image.draw(canvas);
                    getSprites().gPressButtons[3].image.draw(canvas);
                }

                if (creating && getSprites().gBuilding != null && getSprites().aBuilding != null) {
                    getSprites().gBuilding.image.draw(canvas);
                    canvas.save();
                    canvas.rotate(rotation, Float.valueOf(getSprites().gBuilding2.image.getBounds().centerX()), Float.valueOf(getSprites().gBuilding2.image.getBounds().centerY()));
                    getSprites().gBuilding2.image.draw(canvas);
                    canvas.restore();
                    rotation += 10f;
                }

                // Draw Time
                if (mGame.getCurrentRound().time > -1l) {
                    long minutes = (mGame.getCurrentRound().time) / 60000;
                    long seconds = (mGame.getCurrentRound().time) / 1000;
                    if (seconds > 60) {
                        seconds = seconds - (minutes * 60);
                    }
                    if (seconds == 60) {
                        seconds = 0;
                    }
                    String strValue = MessageFormat.format("{0}:{1,number,00}", minutes, seconds);
                    drawNumbers(canvas, 200, 45, strValue, null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                }

            }

            drawNumbers(canvas, 420, 10, mGame.points, null, standardNumberWidth, standardNumberHeight);
        }

        if (mMode == GameState.PLAY && mSubMode == GameSubState.NONE) {
            choiceBaseDraw(canvas, getSprites().rBtnPause, getSprites().gButtonOverlay, getSprites().gButton, activeButton, EngineConstants.ACTION_NONE, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rBtnPause, getString(R.string.menubutton_pause));
        }

        if (mMode == GameState.PLAY && mSubMode == GameSubState.PAUSE) {
            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, Constants.ACTION_HOME, GameValues.cFilterGreen);

            getSprites().gButton.image.setBounds(new Rect(getSprites().rBtnResume));
            getSprites().gButton.image.draw(canvas);

            drawText(canvas, getSprites().rBtnResume, getString(R.string.menubutton_resume));
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_cancel));
        }

        if (mMode == GameState.READY) {
            drawText(canvas, getSprites().rMsgGameState, MessageFormat.format(getString(R.string.message_gameround), mGame.currentRound + 1), 0, 0);
        }

        if (mMode == GameState.END && (mGame.finished() || mGame.complete)) {
            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, Constants.ACTION_HOME, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back));

            if (mGame.won) {
                drawText(canvas, getSprites().rMsgGameState, getString(R.string.message_gamewon), 0, 0);
            } else {
                drawText(canvas, getSprites().rMsgGameState, getString(R.string.message_gamelost), 0, 0);
//                drawText(canvas, getSprites().rMsgScoreAward, MessageFormat.format(getString(R.string.message_bonus_coins), getSprites().pPlayer.score / GameValues.scoreDividerSinglePlayer), 0, 0, GameValues.cFilterYellow);
            }

            drawText(canvas, getSprites().rMsgScore, getString(R.string.menubutton_score));
            drawTextUnboundedScaled(canvas, getSprites().rScore, Integer.toString(mGame.points), GameValues.cFilterYellow);

            // draw score
//            drawNumbers(canvas, 420, 10, mGame.points, null, standardNumberWidth, standardNumberHeight);
        }
    }

    public synchronized void setMode(GameState mode) {
        synchronized (mMode) {
            mMode = mode;
        }
    }

    public synchronized GameState getMode() {
        return mMode;
    }

    public synchronized void setSubMode(GameSubState mode) {
        mSubMode = mode;
    }

    public synchronized GameSubState getSubMode() {
        return mSubMode;
    }

    public void setBonus(boolean b) {
    }

    @Override
    public void actionHandler(int action) {
        // handle click actions directly to the game screen
        getScreen().actionHandler(action);
    }

    public GameViewSprites getSprites() {
        return GameViewSprites.class.cast(super.sprites);
    }


    @Override
    public void reset() {
    }

    @Override
    public float getCharSpacing() {
        return Constants.CHAR_SPACING;
    }

    /**
     * Builder task which creates the game level
     * http://developer.android.com/reference/android/os/AsyncTask.html
     */
    public class BuildTask extends AsyncTask<Object, Object, Object> {

        // Pathfinding
        private HedgehogGameMap mGameMap;
        private PathFinder mFinder;
        private boolean mDesert;

        // local reference to avoid cross-thread access
        public List<Obstacle> gObstacle;

        @Override
        protected Object doInBackground(Object... params) {
            // 2. Step

            creating = true;

            this.gObstacle = new ArrayList<Obstacle>();

            buildLevel();

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            // 4. Step
            creating = false;
            synchronized (getSprites().gObstacle) {
                getSprites().gObstacle.clear();
                getSprites().gObstacle.addAll(this.gObstacle);
            }
            GameRenderer.this.mGameMap = this.mGameMap;
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // 1. Step

            mDesert = rnd.nextBoolean();

            mGameMap = new HedgehogGameMap();
            mGameMap.fillTerrain(mDesert ? EngineConstants.PathFinding.TERRAIN_DESERT : EngineConstants.PathFinding.TERRAIN_GRASS, true, mDesert ? EngineConstants.PathFinding.STATICS_PALM : EngineConstants.PathFinding.STATICS_FOREST);
            mFinder = new AStarPathFinder(mGameMap, 30, false, new ManhattanHeuristic(5));

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            // 3. Step

            super.onProgressUpdate(values);
        }

        /**
         * Invoked from 2. Step
         */
        protected void buildLevel() {

//          TypedArray walls = mContext.getResources().obtainTypedArray(R.array.level_water_tiles);
//          TypedArray trees = mContext.getResources().obtainTypedArray(R.array.level_tree_tiles);
            TypedArray foods = mContext.getResources().obtainTypedArray(R.array.level_food_tiles);
            TypedArray hunter = mContext.getResources().obtainTypedArray(R.array.level_hunter_tiles);
            TypedArray tunnel = mContext.getResources().obtainTypedArray(R.array.level_tunnel_tiles);

            // define obstacle width
            this.gObstacle.clear();

            // design level
            int[] unit = mGameMap.getFreeTile(rnd);
            while (unit[0] < 0 && unit[1] < 0) {
                unit = mGameMap.getFreeTile(rnd);
            }
            mGameMap.setUnit(unit[0], unit[1], EngineConstants.PathFinding.UNIT_HERO);

            // create Paths
            int l = rnd.nextInt(GameValues.maxPathsPerRound);
            if (l < GameValues.minPathsPerRound) {
                l = GameValues.minPathsPerRound;
            }
            for (int i = 0; i < l; i++) {
                int length = rnd.nextInt(GameValues.maxPathLength);
                if (length < GameValues.minPathLength) {
                    length = GameValues.minPathLength;
                }
                createPath(unit[0], unit[1], length);
            }

            // design random deserts
//          int dMax = rnd.nextInt(GameValues.maxDesertsPerRound);
//          if(dMax < GameValues.minDesertsPerRound) {
//              dMax = GameValues.minDesertsPerRound;
//          }
//          createAreas(dMax, 3, GameMap.TERRAIN_DESERT, false);

            // determine tree stumps
            int k = rnd.nextInt(GameValues.maxTreesPerRound);
            if (k < GameValues.minTreesPerRound) {
                k = GameValues.minTreesPerRound;
            }
            createAreas(k, GameValues.startPositionSafeFrameObstacles, mDesert ? EngineConstants.PathFinding.STATICS_PALM : EngineConstants.PathFinding.STATICS_FOREST, true);
//          createAreas(k, GameValues.startPositionSafeFrameObstacles, GameMap.STATICS_STONES, true);
//          createObject(GameValues.TYPE_WALL, k, trees, GameValues.startPositionSafeFrameObstacles, GameValues.scaleTrees);

            // create rivers
            int r = rnd.nextInt(GameValues.maxRiversPerRound);
            if (r < GameValues.minRiversPerRound) {
                r = GameValues.minRiversPerRound;
            }
            for (int i = 0; i < r; i++) {
                int length = rnd.nextInt(GameValues.maxRiverLength);
                if (length < GameValues.minRiverLength) {
                    length = GameValues.minRiverLength;
                }
                createRiver(0, 0, 0, length, rnd.nextBoolean());
            }

            // determine trees
            int t = rnd.nextInt(GameValues.maxRandomTreesPerRound);
            if (t < GameValues.minRandomTreesPerRound) {
                t = GameValues.minRandomTreesPerRound;
            }
            createSingleStatics(mDesert ? EngineConstants.PathFinding.STATICS_PALM : EngineConstants.PathFinding.STATICS_FOREST, t);

            // determine trees
            int p = rnd.nextInt(GameValues.maxRandomPoolsPerRound);
            if (p < GameValues.minRandomPoolsPerRound) {
                p = GameValues.minRandomPoolsPerRound;
            }
            createSingleStatics(EngineConstants.PathFinding.STATICS_POOL, p);

            // determine food positions
            int j = rnd.nextInt(GameValues.maxFoodPerRound);
            if (j < GameValues.minFoodPerRound) {
                j = GameValues.minFoodPerRound;
            }
            createObject(EngineConstants.PathFinding.OBJECT_FOOD, j, foods, -1, true);

            // determine hunter positions
            int h = rnd.nextInt(GameValues.maxHunterPerRound);
            if (h < GameValues.minHunterPerRound) {
                h = GameValues.minHunterPerRound;
            }
            createObject(EngineConstants.PathFinding.UNIT_HUNTER, h, hunter, GameValues.startPositionSafeFrameHunter, false);

            createObject(EngineConstants.PathFinding.OBJECT_TUNNEL, 2, tunnel, GameValues.startPositionSafeFrameObstacles, true);

        }

        private void createAreas(int max, int safeFrame, int type, boolean checkPath) {
            for (int d = 0; d < max; d++) {
                int[] desert = mGameMap.getFreeTile(rnd);
                if (desert[0] > -1) {
                    int spaceLR = rnd.nextInt(6);
                    if (spaceLR <= 0) {
                        spaceLR = 4;
                    }
                    int spaceTB = rnd.nextInt(6);
                    if (spaceTB <= 0) {
                        spaceTB = 4;
                    }
                    int i = desert[0] - spaceLR / 2;
                    if (i < 0) {
                        i = 0;
                    }
                    for (; i < desert[0] + spaceLR && i < GameValues.GAMETILES_X; i++) {
                        int j = desert[1] - spaceTB / 2;
                        if (j < 0) {
                            j = 0;
                        }
                        for (; j < desert[1] + spaceTB && j < GameValues.GAMETILES_Y; j++) {
                            if (!mGameMap.blocked(EngineConstants.PathFinding.UNIT_HERO, i, j) && (!checkPath || (checkPath && !mGameMap.isPath(i, j)))) {
                                mGameMap.setStatics(i, j, type);
                            }
                        }
                    }
                }
            }
        }


        private void createSingleStatics(int type, int max) {
            for (int d = 0; d < max; d++) {
                int[] tile = mGameMap.getFreeTile(rnd);
                if (tile[0] > -1) {
                    if (!mGameMap.blocked(EngineConstants.PathFinding.UNIT_HERO, tile[0], tile[1]) && !mGameMap.isPath(tile[0], tile[1])) {
                        mGameMap.setStatics(tile[0], tile[1], type);
                    }
                }
            }
        }

        private void createObject(int objType, int max, TypedArray objArray, int safeFrame, boolean absoluteFree) {
            int[] unit = mGameMap.getHeroUnitPosition();
            // create object
            for (int min = 0; min < max; min++) {
                // get the next free tile
                int[] tile = mGameMap.getFreeTile(rnd);
                // skip execution, no tile assigned
                if (tile[0] == -1 || (unit[0] == tile[0] && unit[1] == tile[1])) {
                    continue;
                }
                boolean blocked = isTileFree(tile[0], tile[1]);
                int safety = 0;
                while (absoluteFree && blocked && safety < 5) {
                    tile = mGameMap.getFreeTile(rnd);
                    safety++;
                }

                Obstacle o = new Obstacle(objType, 0.10f, rnd.nextInt(objArray.length()), 10);
                o.x = tile[0];
                o.y = tile[1];
                this.gObstacle.add(o);
            }
        }

        /**
         * Check if the tile is not blocked by obstacles
         *
         * @param x
         * @param y
         * @return
         */
        private boolean isTileFree(int x, int y) {
            for (Obstacle obstacle : this.gObstacle) {
                if (obstacle.x == x && obstacle.y == y) {
                    return true;
                }
            }
            return false;
        }

        private void createRiver(int tileX, int tileY, int tilePos, int max, boolean vertical) {

            // check object intersection - safety check if no position could be allocated
            // get the next free tile
            int[] tile = tilePos == 0 ? mGameMap.getFreeTile(rnd) : mGameMap.getNextFreeTile(tileX, tileY);

            // self update to keep drawing
            tileX = tile[0];
            tileY = tile[1];

            // skip execution, no tile assigned
            if (tileX == -1) {
                return;
            }

            float rotation = 0.0f;
            int terrain = EngineConstants.PathFinding.TERRAIN_WATER;
            if (vertical) {
                rotation += 90.0f;
                if (tilePos == 0) {
                    terrain = EngineConstants.PathFinding.STATICS_WATEREND;
                    rotation += 0.0f;
                } else if (tilePos == max) {
                    terrain = EngineConstants.PathFinding.STATICS_WATEREND;
                    rotation += 180.0f;
                } else {
                    terrain = EngineConstants.PathFinding.STATICS_WATERMIDDLE;
                }
            } else {
                // horizontal
                if (tilePos == 0) {
                    terrain = EngineConstants.PathFinding.STATICS_WATEREND;
                    rotation += 0.0f;
                } else if (tilePos == max) {
                    terrain = EngineConstants.PathFinding.STATICS_WATEREND;
                    rotation += 180.0f;
                } else {
                    terrain = EngineConstants.PathFinding.STATICS_WATERMIDDLE;
                }
            }

            // block game tile
            mGameMap.setStatics(tileX, tileY, terrain, rotation);

            // loop into next path tile
            if (tilePos++ < max) {
                int nextX = vertical ? tileX : tileX + 1;
                int nextY = vertical ? tileY + 1 : tileY;
                createRiver(nextX, nextY, tilePos++, max, vertical);
            }
        }

        private void createPath(int tileX, int tileY, int max) {
            // get the next free tile
            int[] tile = mGameMap.getFreeTile(rnd);
            mGameMap.clearVisited();
            int targetTileX = tile[0];
            int targetTileY = tile[1];
            Path mPath = mFinder.findPath(new UnitMover(EngineConstants.PathFinding.UNIT_HERO), tileX, tileY, targetTileX, targetTileY);
            if (mPath != null) {
                for (int i = 0; i < mPath.getLength(); i++) {
                    Step step = mPath.getStep(i);
                    mGameMap.setPath(step.x, step.y);
                }
            }
        }

    }


}
