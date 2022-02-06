package com.juergenkleck.android.game.hedgehog.rendering.objects;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Obstacle extends com.juergenkleck.android.gameengine.rendering.objects.Obstacle {

    public Obstacle(int type, float life, int gReference, int points) {
        super(type, life, gReference, points);
    }

    // movement time
    public long lastMove;

    // the obstacle position on the game map
    public int x;
    public int y;

}
