package info.simplyapps.game.hedgehog.storage.dto;

import java.io.Serializable;

import info.simplyapps.appengine.storage.dto.BasicTable;

public class CurrentGame extends BasicTable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6974204243261183587L;
    public int round;
    // the available life
    public float life;
    // the points in the level
    public int points;
    public int coins;

    public long time;

}
