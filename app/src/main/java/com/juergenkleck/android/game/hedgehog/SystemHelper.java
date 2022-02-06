package com.juergenkleck.android.game.hedgehog;

import com.juergenkleck.android.game.hedgehog.storage.StoreData;
import com.juergenkleck.android.game.hedgehog.storage.dto.CurrentGame;
import com.juergenkleck.android.game.hedgehog.storage.dto.Inventory;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class SystemHelper extends com.juergenkleck.android.appengine.SystemHelper {

    public synchronized static final Inventory getInventory() {
        return StoreData.getInstance().inventory;
    }

    public synchronized static final CurrentGame getCurrentGame() {
        return StoreData.getInstance().currentGame;
    }

}
