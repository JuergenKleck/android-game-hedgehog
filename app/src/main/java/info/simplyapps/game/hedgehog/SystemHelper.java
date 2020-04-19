package info.simplyapps.game.hedgehog;

import info.simplyapps.game.hedgehog.storage.StoreData;
import info.simplyapps.game.hedgehog.storage.dto.CurrentGame;
import info.simplyapps.game.hedgehog.storage.dto.Inventory;

public class SystemHelper extends info.simplyapps.appengine.SystemHelper {

    public synchronized static final Inventory getInventory() {
        return StoreData.getInstance().inventory;
    }

    public synchronized static final CurrentGame getCurrentGame() {
        return StoreData.getInstance().currentGame;
    }

}
