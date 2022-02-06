package com.juergenkleck.android.game.hedgehog.storage;

import com.juergenkleck.android.appengine.AppEngineConstants;
import com.juergenkleck.android.appengine.storage.dto.Configuration;
import com.juergenkleck.android.game.hedgehog.SystemHelper;
import com.juergenkleck.android.game.hedgehog.storage.dto.CurrentGame;
import com.juergenkleck.android.game.hedgehog.storage.dto.Inventory;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class StoreData extends com.juergenkleck.android.appengine.storage.StoreData {

    public Inventory inventory;
    public CurrentGame currentGame;

    /**
     *
     */
    private static final long serialVersionUID = 2982830586304674266L;

    public static StoreData getInstance() {
        return (StoreData) com.juergenkleck.android.appengine.storage.StoreData.getInstance();
    }

    @Override
    public boolean update() {
        boolean persist = false;

        // Release 1 - 1.0
        if (migration < 1) {
            inventory = new Inventory();
            currentGame = new CurrentGame();
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_ON_SERVER, AppEngineConstants.DEFAULT_CONFIG_ON_SERVER));
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_FORCE_UPDATE, AppEngineConstants.DEFAULT_CONFIG_FORCE_UPDATE));
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_LAST_CHECK, AppEngineConstants.DEFAULT_CONFIG_LAST_CHECK));
            persist = true;
        }

        // Release 2 - 1.0
        if (migration < 2) {
        }

        // Release 3 - 1.0
        if (migration < 3) {
        }

        // Release 4 - 1.1
        if (migration < 4) {
        }

        migration = 4;
        return persist;
    }

}
