package info.simplyapps.game.hedgehog.storage.dto;

import java.io.Serializable;

import info.simplyapps.appengine.storage.dto.BasicTable;
import info.simplyapps.game.hedgehog.engine.GameValues;

public class Inventory extends BasicTable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6974204243261183587L;
    public long coins;
    public long points;

    public long gamesWon;
    public long gamesLost;

    public int game1;
    public int game2;
    public int game3;
    public int game4;
    public int game5;

    public int difficulty;

    public boolean music;

    public Inventory() {
        coins = 0l;
        points = 0l;
        gamesWon = 0l;
        gamesLost = 0l;
        game1 = 0;
        game2 = 0;
        game3 = 0;
        game4 = 0;
        game5 = 0;
        difficulty = GameValues.DIFFICULTY_EASY;
    }

}
