package com.juergenkleck.android.game.hedgehog.engine;

import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.pathfinding.impl.BasicGameMap;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class HedgehogGameMap extends BasicGameMap {

    /**
     * Create a new map with some default configuration
     */
    public HedgehogGameMap() {
        super();
    }

    @Override
    public int getGameTilesX() {
        return GameValues.GAMETILES_X;
    }

    @Override
    public int getGameTilesY() {
        return GameValues.GAMETILES_Y;
    }

    @Override
    public int[] getInmoveables() {
        return new int[]{
                EngineConstants.PathFinding.TERRAIN_STONES
                , EngineConstants.PathFinding.TERRAIN_FOREST
                , EngineConstants.PathFinding.TERRAIN_WATER
                , EngineConstants.PathFinding.STATICS_WATEREND
                , EngineConstants.PathFinding.STATICS_WATERMIDDLE
                , EngineConstants.PathFinding.STATICS_FOREST
                , EngineConstants.PathFinding.STATICS_POOL
                , EngineConstants.PathFinding.STATICS_STONES
                , EngineConstants.PathFinding.STATICS_PALM
        };
    }

    @Override
    public int[] getMoveables() {
        return new int[]{
                EngineConstants.PathFinding.TERRAIN_GRASS
                , EngineConstants.PathFinding.TERRAIN_DESERT
        };
    }

    @Override
    public int getInitTerrain() {
        return EngineConstants.PathFinding.TERRAIN_GRASS;
    }

    @Override
    public int getInitBorder() {
        return EngineConstants.PathFinding.STATICS_FOREST;
    }

}