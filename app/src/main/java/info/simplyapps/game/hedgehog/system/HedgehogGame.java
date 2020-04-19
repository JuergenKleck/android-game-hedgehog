package info.simplyapps.game.hedgehog.system;

import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.pathfinding.impl.UnitMover;
import info.simplyapps.gameengine.pathfinding.interfaces.Mover;
import info.simplyapps.gameengine.system.Game;
import info.simplyapps.gameengine.system.GameRound;

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
