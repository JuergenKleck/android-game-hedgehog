package com.juergenkleck.android.game.hedgehog.system;

import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.pathfinding.impl.UnitMover;
import com.juergenkleck.android.gameengine.pathfinding.interfaces.Mover;
import com.juergenkleck.android.gameengine.system.Game;
import com.juergenkleck.android.gameengine.system.GameRound;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class HedgehogGame extends Game {

    public boolean lifeLost;
    public boolean won;
    public boolean complete;

    public boolean inTunnel;
    public Mover mover;

    public HedgehogGame(GameRound[] rounds) {
        super(rounds);
        mover = new UnitMover(EngineConstants.PathFinding.UNIT_HERO);
    }

    public void reset() {
        super.reset();
        won = false;
        lifeLost = false;
        life = 0.0f;
    }

}
