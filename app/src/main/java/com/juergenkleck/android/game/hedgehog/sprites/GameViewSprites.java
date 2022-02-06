package com.juergenkleck.android.game.hedgehog.sprites;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import com.juergenkleck.android.game.hedgehog.rendering.objects.Obstacle;
import com.juergenkleck.android.gameengine.rendering.objects.Animation;
import com.juergenkleck.android.gameengine.rendering.objects.Graphic;
import com.juergenkleck.android.gameengine.sprites.ViewSprites;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
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
