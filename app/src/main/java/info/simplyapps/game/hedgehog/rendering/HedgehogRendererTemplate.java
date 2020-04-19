package info.simplyapps.game.hedgehog.rendering;

import android.content.Context;

import java.util.Properties;

import info.simplyapps.gameengine.rendering.GenericRendererTemplate;

public abstract class HedgehogRendererTemplate extends GenericRendererTemplate {

    public HedgehogRendererTemplate(Context context, Properties properties) {
        super(context, properties);
    }

    public boolean logEnabled() {
        return false;
    }

}
