package com.juergenkleck.android.game.hedgehog.storage.dto;

import java.io.Serializable;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
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
