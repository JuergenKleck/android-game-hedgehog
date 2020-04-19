package info.simplyapps.game.hedgehog.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.Properties;

import info.simplyapps.game.hedgehog.Constants;
import info.simplyapps.game.hedgehog.R;
import info.simplyapps.game.hedgehog.engine.GameValues;
import info.simplyapps.game.hedgehog.sprites.HomeViewSprites;

public class InfoRenderer extends HedgehogRendererTemplate {

    Rect mRect;
    long delay = 100l;
    long lastTime = 0l;
    boolean triggered = false;

    public InfoRenderer(Context context, Properties p) {
        super(context, p);
    }

    public HomeViewSprites getSprites() {
        return HomeViewSprites.class.cast(super.sprites);
    }

    @Override
    public void doStart() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void unpause() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void doUpdateRenderState() {
        final long time = System.currentTimeMillis();

        if (delay > 0l && lastTime > 0l) {
            delay -= time - lastTime;
        }
        if (delay <= 0l && !triggered) {
            instantActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
            triggered = true;
        }

        lastTime = time;
    }

    @Override
    public void doDrawRenderer(Canvas canvas) {
        drawText(canvas, mRect, getString(R.string.creator), scaleWidth(Constants.spaceLR), scaleHeight(Constants.spaceTB), GameValues.cFilterYellow);
    }


    @Override
    public void restoreGameState() {
    }

    @Override
    public void saveGameState() {
    }

    @Override
    public void doInitThread(long time) {
        super.sprites = new HomeViewSprites();

        mRect = new Rect(0, 0, screenWidth, screenHeight);
        mRect.bottom -= mRect.height() / 2;
        mRect.offsetTo(0, (screenHeight - mRect.height()) / 2);
    }

    @Override
    public void reset() {
    }

    @Override
    public float getCharSpacing() {
        return Constants.CHAR_SPACING;
    }
}
