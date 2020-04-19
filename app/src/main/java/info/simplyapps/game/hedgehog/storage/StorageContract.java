package info.simplyapps.game.hedgehog.storage;

import android.provider.BaseColumns;

public class StorageContract extends
        info.simplyapps.appengine.storage.StorageContract {

    public static abstract class TableInventory implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_COINS = "coins";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_GAMESWON = "gameswon";
        public static final String COLUMN_GAMESLOST = "gameslost";
        public static final String COLUMN_GAME1 = "game1";
        public static final String COLUMN_GAME2 = "game2";
        public static final String COLUMN_GAME3 = "game3";
        public static final String COLUMN_GAME4 = "game4";
        public static final String COLUMN_GAME5 = "game5";
        public static final String COLUMN_MUSIC = "music";
    }

    public static abstract class TableCurrentGame implements BaseColumns {
        public static final String TABLE_NAME = "currentgame";
        public static final String COLUMN_ROUND = "round";
        public static final String COLUMN_LIFE = "life";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_COINS = "coins";
        public static final String COLUMN_TIME = "time";
    }

}
