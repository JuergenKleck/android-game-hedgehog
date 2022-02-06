package com.juergenkleck.android.game.hedgehog.engine;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class GameValues extends com.juergenkleck.android.gameengine.system.GameValues {

    /**
     * The map width in tiles
     */
    public static final int GAMEMAP_WIDTH = 16; // equals 960 width 
    /**
     * The map height in tiles
     */
    public static final int GAMEMAP_HEIGHT = 10; // equals 540 height

    public static final int GAMETILES_X = 32;
    public static final int GAMETILES_Y = 32;

    public static final int DRAW_FOOD = 0;
    public static final int DRAW_WALL = 1;
    public static final int DRAW_TRAP = 2;
    public static final int DRAW_HUNTER = 3;
    public static final int DRAW_TUNNEL = 4;

    public static final long gameRoundDelay = 0000l;
    public static final long movementDelay = 150l;
    public static final int movementPixel = 15;
    public static final long nightDimming = 150l;
    public static final float hunterSpeedEasy = 1.0f;
    public static final float hunterSpeedMedium = 0.75f;
    public static final float hunterSpeedHard = 0.50f;
    public static final long hunterDelay = 2000l;

    public static final float totalLife = 1.0f;

    public static final int roundTime = 300000;
    public static final int roundTimeMedium = 270000;
    public static final int roundTimeHard = 240000;

    public static final int startPositionSafeFrameHunter = 5;
    public static final int startPositionSafeFrameObstacles = 2;
    public static final int maxFoodPerRound = 30;
    public static final int minFoodPerRound = 5;
    public static final int maxHunterPerRound = 4;
    public static final int minHunterPerRound = 1;
    public static final int maxPathsPerRound = 10;
    public static final int minPathsPerRound = 2;
    public static final int maxPathLength = 10;
    public static final int minPathLength = 2;
    public static final int maxRiversPerRound = 10;
    public static final int minRiversPerRound = 2;
    public static final int maxRiverLength = 10;
    public static final int minRiverLength = 2;
    public static final int maxTreesPerRound = 5;
    public static final int minTreesPerRound = 1;
    public static final int maxDesertsPerRound = 3;
    public static final int minDesertsPerRound = 1;
    public static final int maxRandomTreesPerRound = 300;
    public static final int minRandomTreesPerRound = 100;
    public static final int maxRandomPoolsPerRound = 20;
    public static final int minRandomPoolsPerRound = 5;

    public static final int scoreDividerSinglePlayer = 8;

}
