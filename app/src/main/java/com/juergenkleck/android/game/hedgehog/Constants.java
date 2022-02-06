package com.juergenkleck.android.game.hedgehog;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Constants {

    public static final String DATABASE = "hedgehog.db";
    public static final int DATABASE_VERSION = 1;
    public static final String PREFERENCE_NS = "http://com.juergenkleck.android.game.hedgehog.rendering";
    public static final int spaceLR = 10;
    public static final int spaceTB = 8;
    public static final int spaceTBWide = 48;
    public static final int spaceMainBtnLR = 30;
    public static final int spaceMainBtnTB = 46;
    public static final float CHAR_SPACING = 0.35f;

    public static final int ACTION_SCORE = 101;
    public static final int ACTION_HOME = 300;

    public static final long buttonPressTime = 2000l;

    public enum RenderMode {
        HOME, GAME, SCORE, OPTIONS, WAIT;
    }

    public final class GameProperties {
        public static final String GAMEMAP_W = "gamemapW";
        public static final String GAMEMAP_H = "gamemapH";
    }

}
