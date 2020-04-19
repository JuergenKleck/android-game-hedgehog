package info.simplyapps.game.hedgehog.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import info.simplyapps.appengine.storage.dto.BasicTable;
import info.simplyapps.game.hedgehog.storage.dto.CurrentGame;
import info.simplyapps.game.hedgehog.storage.dto.Inventory;

public class DBDriver extends info.simplyapps.appengine.storage.DBDriver {

    private static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + StorageContract.TableInventory.TABLE_NAME + " (" +
                    StorageContract.TableInventory._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableInventory.COLUMN_COINS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_POINTS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAMESWON + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAMESLOST + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAME1 + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAME2 + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAME3 + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAME4 + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAME5 + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_MUSIC + TYPE_INT +
                    " );";
    private static final String SQL_DELETE_INVENTORY =
            "DROP TABLE IF EXISTS " + StorageContract.TableInventory.TABLE_NAME;

    private static final String SQL_CREATE_CURRENTGAME =
            "CREATE TABLE " + StorageContract.TableCurrentGame.TABLE_NAME + " (" +
                    StorageContract.TableCurrentGame._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableCurrentGame.COLUMN_ROUND + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_LIFE + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_POINTS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_COINS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_TIME + TYPE_INT +
                    " );";
    private static final String SQL_DELETE_CURRENTGAME =
            "DROP TABLE IF EXISTS " + StorageContract.TableCurrentGame.TABLE_NAME;

    public DBDriver(String dataBaseName, int dataBaseVersion, Context context) {
        super(dataBaseName, dataBaseVersion, context);
    }

    public static DBDriver getInstance() {
        return (DBDriver) info.simplyapps.appengine.storage.DBDriver.getInstance();
    }

    @Override
    public void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INVENTORY);
        db.execSQL(SQL_CREATE_CURRENTGAME);
    }


    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public String getExtendedTable(BasicTable data) {
        return Inventory.class.isInstance(data) ? StorageContract.TableInventory.TABLE_NAME :
                CurrentGame.class.isInstance(data) ? StorageContract.TableCurrentGame.TABLE_NAME : null;
    }

    @Override
    public void storeExtended(info.simplyapps.appengine.storage.StoreData data) {
        store(StoreData.class.cast(data).inventory);
        store(StoreData.class.cast(data).currentGame);
    }

    @Override
    public void readExtended(info.simplyapps.appengine.storage.StoreData data, SQLiteDatabase db) {
        readInventory(StoreData.class.cast(data), db);
        readCurrentGame(StoreData.class.cast(data), db);
    }

    @Override
    public info.simplyapps.appengine.storage.StoreData createStoreData() {
        return new StoreData();
    }

    public boolean store(Inventory data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableInventory.COLUMN_COINS, data.coins);
        values.put(StorageContract.TableInventory.COLUMN_POINTS, data.points);
        values.put(StorageContract.TableInventory.COLUMN_GAMESWON, data.gamesWon);
        values.put(StorageContract.TableInventory.COLUMN_GAMESLOST, data.gamesLost);
        values.put(StorageContract.TableInventory.COLUMN_GAME1, data.game1);
        values.put(StorageContract.TableInventory.COLUMN_GAME2, data.game2);
        values.put(StorageContract.TableInventory.COLUMN_GAME3, data.game3);
        values.put(StorageContract.TableInventory.COLUMN_GAME4, data.game4);
        values.put(StorageContract.TableInventory.COLUMN_GAME5, data.game5);
        values.put(StorageContract.TableInventory.COLUMN_MUSIC, data.music ? 1 : 0);
        return persist(data, values, StorageContract.TableInventory.TABLE_NAME);
    }

    private void readInventory(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableInventory._ID,
                StorageContract.TableInventory.COLUMN_COINS,
                StorageContract.TableInventory.COLUMN_POINTS,
                StorageContract.TableInventory.COLUMN_GAMESWON,
                StorageContract.TableInventory.COLUMN_GAMESLOST,
                StorageContract.TableInventory.COLUMN_GAME1,
                StorageContract.TableInventory.COLUMN_GAME2,
                StorageContract.TableInventory.COLUMN_GAME3,
                StorageContract.TableInventory.COLUMN_GAME4,
                StorageContract.TableInventory.COLUMN_GAME5,
                StorageContract.TableInventory.COLUMN_MUSIC
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                StorageContract.TableInventory.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasResults = c.moveToFirst();
        while (hasResults) {
            Inventory i = new Inventory();
            i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory._ID));
            i.coins = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_COINS));
            i.points = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_POINTS));
            i.gamesWon = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAMESWON));
            i.gamesLost = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAMESLOST));
            i.game1 = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAME1));
            i.game2 = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAME2));
            i.game3 = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAME3));
            i.game4 = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAME4));
            i.game5 = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAME5));
            i.music = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_MUSIC)) == 1;
            data.inventory = i;
            hasResults = c.moveToNext();
        }
        c.close();
    }

    public boolean store(CurrentGame data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableCurrentGame.COLUMN_ROUND, data.round);
        values.put(StorageContract.TableCurrentGame.COLUMN_LIFE, data.life);
        values.put(StorageContract.TableCurrentGame.COLUMN_POINTS, data.points);
        values.put(StorageContract.TableCurrentGame.COLUMN_COINS, data.coins);
        values.put(StorageContract.TableCurrentGame.COLUMN_TIME, data.time);
        return persist(data, values, StorageContract.TableCurrentGame.TABLE_NAME);
    }

    private void readCurrentGame(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableCurrentGame._ID,
                StorageContract.TableCurrentGame.COLUMN_ROUND,
                StorageContract.TableCurrentGame.COLUMN_LIFE,
                StorageContract.TableCurrentGame.COLUMN_POINTS,
                StorageContract.TableCurrentGame.COLUMN_COINS,
                StorageContract.TableCurrentGame.COLUMN_TIME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                StorageContract.TableCurrentGame.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasResults = c.moveToFirst();
        while (hasResults) {
            CurrentGame i = new CurrentGame();
            i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame._ID));
            i.round = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_ROUND));
            i.life = c.getFloat(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_LIFE));
            i.points = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_POINTS));
            i.coins = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_COINS));
            i.time = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_TIME));
            data.currentGame = i;
            hasResults = c.moveToNext();
        }
        c.close();
    }

}
