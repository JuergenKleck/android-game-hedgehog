package info.simplyapps.game.hedgehog.screens;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Properties;

import info.simplyapps.game.hedgehog.Constants;
import info.simplyapps.game.hedgehog.engine.GameValues;
import info.simplyapps.game.hedgehog.rendering.HomeRenderer;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.RenderingSystem;
import info.simplyapps.gameengine.rendering.GenericViewTemplate;

public class HomeView extends GenericViewTemplate {

    // Attribute names
    private static final String ATTR_GAMEMAP_WIDTH_VALUE = "gameMapWidth";
    private static final String ATTR_GAMEMAP_HEIGHT_VALUE = "gameMapHeight";
    // Default values for defaults
    private static final int GAMEMAP_WIDTH_CURRENT_VALUE = GameValues.GAMEMAP_WIDTH;
    private static final int GAMEMAP_HEIGHT_CURRENT_VALUE = GameValues.GAMEMAP_HEIGHT;
    // Real defaults
    private final int mGameMapWidth;
    private final int mGameMapHeight;

    public HomeView(Context context) {
        super(context);
        mGameMapWidth = GAMEMAP_WIDTH_CURRENT_VALUE;
        mGameMapHeight = GAMEMAP_HEIGHT_CURRENT_VALUE;
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGameMapWidth = attrs.getAttributeIntValue(Constants.PREFERENCE_NS, ATTR_GAMEMAP_WIDTH_VALUE, GAMEMAP_WIDTH_CURRENT_VALUE);
        mGameMapHeight = attrs.getAttributeIntValue(Constants.PREFERENCE_NS, ATTR_GAMEMAP_HEIGHT_VALUE, GAMEMAP_HEIGHT_CURRENT_VALUE);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mGameMapWidth = attrs.getAttributeIntValue(Constants.PREFERENCE_NS, ATTR_GAMEMAP_WIDTH_VALUE, GAMEMAP_WIDTH_CURRENT_VALUE);
        mGameMapHeight = attrs.getAttributeIntValue(Constants.PREFERENCE_NS, ATTR_GAMEMAP_HEIGHT_VALUE, GAMEMAP_HEIGHT_CURRENT_VALUE);
    }

    @Override
    public void createThread(Context context) {
        Properties p = new Properties();
        p.put(EngineConstants.GameProperties.RENDERING_SYSTEM, RenderingSystem.SINGLE_PLAYER);
        p.put(EngineConstants.GameProperties.SCREEN_SCALE, getScreenScaleValue());
        p.put(EngineConstants.GameProperties.LEVEL, 0);

        setBasicEngine(new HomeRenderer(getContext(), p));
    }

    @Override
    public boolean isDragable() {
        return true;
    }

    @Override
    public String getNameSpace() {
        return Constants.PREFERENCE_NS;
    }

    public int getGameMapWidth() {
        return mGameMapWidth;
    }

    public int getGameMapHeight() {
        return mGameMapHeight;
    }

}
