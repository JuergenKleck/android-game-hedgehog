package info.simplyapps.game.hedgehog.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.text.MessageFormat;
import java.util.Properties;
import java.util.Random;

import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.game.hedgehog.Constants;
import info.simplyapps.game.hedgehog.Constants.RenderMode;
import info.simplyapps.game.hedgehog.R;
import info.simplyapps.game.hedgehog.SystemHelper;
import info.simplyapps.game.hedgehog.engine.GameValues;
import info.simplyapps.game.hedgehog.sprites.HomeViewSprites;
import info.simplyapps.game.hedgehog.storage.DBDriver;
import info.simplyapps.game.hedgehog.storage.dto.CurrentGame;
import info.simplyapps.game.hedgehog.storage.dto.Inventory;
import info.simplyapps.gameengine.EngineConstants;
import info.simplyapps.gameengine.rendering.kits.Renderkit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit;
import info.simplyapps.gameengine.rendering.kits.ScreenKit.ScreenPosition;

public class HomeRenderer extends HedgehogRendererTemplate {

    Random rnd;
    public RenderMode mRenderMode;
    Paint mLayer;
    Paint mLayerBorder;
    Configuration cDifficulty = null;
    Inventory inventory = null;

    public HomeRenderer(Context context, Properties p) {
        super(context, p);
        mRenderMode = RenderMode.HOME;
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
        if (delayedAction == EngineConstants.ACTION_NONE) {
            // determine button click

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if (Constants.RenderMode.HOME == mRenderMode) {
                        if (containsClick(getSprites().rBtnStart, event.getX(), event.getY())) {
                            delayedActionHandler(EngineConstants.ACTION_START, EngineConstants.ACTION_START);
                        }
                        if (containsClick(getSprites().rBtnScore, event.getX(), event.getY())) {
                            inventory = SystemHelper.getInventory();
                            delayedActionHandler(Constants.ACTION_SCORE, Constants.ACTION_SCORE);
                        }
                        if (containsClick(getSprites().rBtnOptions, event.getX(), event.getY())) {
                            cDifficulty = SystemHelper.getConfiguration(EngineConstants.CONFIG_DIFFICULTY, EngineConstants.DEFAULT_CONFIG_DIFFICULTY);
                            delayedActionHandler(EngineConstants.ACTION_OPTIONS, EngineConstants.ACTION_OPTIONS);
                        }
                    } else if (Constants.RenderMode.OPTIONS == mRenderMode) {
                        boolean difficultyChanged = false;
                        if (containsClick(getSprites().rButtonDifficultEasy, event.getX(), event.getY())) {
                            difficultyChanged = !cDifficulty.value.equals(Integer.toString(GameValues.DIFFICULTY_EASY));
                            cDifficulty.value = Integer.toString(GameValues.DIFFICULTY_EASY);
                            DBDriver.getInstance().store(cDifficulty);
                            SystemHelper.setConfiguration(cDifficulty);
                        }
                        if (containsClick(getSprites().rButtonDifficultMedium, event.getX(), event.getY())) {
                            difficultyChanged = !cDifficulty.value.equals(Integer.toString(GameValues.DIFFICULTY_MEDIUM));
                            cDifficulty.value = Integer.toString(GameValues.DIFFICULTY_MEDIUM);
                            DBDriver.getInstance().store(cDifficulty);
                            SystemHelper.setConfiguration(cDifficulty);
                        }
                        if (containsClick(getSprites().rButtonDifficultHard, event.getX(), event.getY())) {
                            difficultyChanged = !cDifficulty.value.equals(Integer.toString(GameValues.DIFFICULTY_HARD));
                            cDifficulty.value = Integer.toString(GameValues.DIFFICULTY_HARD);
                            DBDriver.getInstance().store(cDifficulty);
                            SystemHelper.setConfiguration(cDifficulty);
                        }
                        if (difficultyChanged) {
                            // reset the game to avoid score cheating
                            CurrentGame cg = SystemHelper.getCurrentGame();
                            cg.life = 0.0f;
                            DBDriver.getInstance().store(cg);
                        }
                        // back
                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                        }
                    } else if (Constants.RenderMode.SCORE == mRenderMode) {
                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                        }
                    } else if (Constants.RenderMode.GAME == mRenderMode) {

                    }

                    break;
            }

        }

        return true;
    }

    @Override
    public void doUpdateRenderState() {

        if (Constants.RenderMode.OPTIONS == mRenderMode) {
        } else if (Constants.RenderMode.GAME == mRenderMode) {
            mRenderMode = Constants.RenderMode.HOME;
        }
    }

    @Override
    public void doDrawRenderer(Canvas canvas) {

        if (getSprites().gBackground != null) {
            // draw image across screen
            int h = 0;
            int v = 0;
            while (h < screenWidth && v < screenHeight) {
                getSprites().gBackground.image.draw(canvas);
                Rect r = getSprites().gBackground.image.copyBounds();
                h = r.right;
                if (h > screenWidth) {
                    v = r.bottom;
                    h = 0;
                }
                r.offsetTo(h, v);
                getSprites().gBackground.image.setBounds(r);
                if (v > screenHeight) {
                    r.offsetTo(0, 0);
                    getSprites().gBackground.image.setBounds(r);
                    break;
                }
            }
        }

        if (Constants.RenderMode.HOME == mRenderMode) {
            getSprites().gLogo.image.draw(canvas);
            getSprites().gTextLogo.image.draw(canvas);

            // draw buttons last to overlay the background items
            choiceBaseDraw(canvas, getSprites().rBtnStart, getSprites().gButtonMain, getSprites().gButtonMain, activeButton, EngineConstants.ACTION_START, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rBtnScore, getSprites().gButtonMain, getSprites().gButtonMain, activeButton, Constants.ACTION_SCORE, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rBtnOptions, getSprites().gButtonMain, getSprites().gButtonMain, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterGreen);

            drawText(canvas, getSprites().rBtnStart, getString(R.string.menubutton_start), ScreenKit.scaleWidth(Constants.spaceMainBtnLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceMainBtnTB, screenHeight));
            drawText(canvas, getSprites().rBtnScore, getString(R.string.menubutton_score), ScreenKit.scaleWidth(Constants.spaceMainBtnLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceMainBtnTB, screenHeight));
            drawText(canvas, getSprites().rBtnOptions, getString(R.string.menubutton_options), ScreenKit.scaleWidth(Constants.spaceMainBtnLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceMainBtnTB, screenHeight));

        } else if (Constants.RenderMode.OPTIONS == mRenderMode) {
            drawLayer(canvas);

            drawTextUnboundedScaled(canvas, getSprites().rMsgDifficulty, getString(R.string.options_difficulty), GameValues.cFilterBlue);

            choiceBaseDraw(canvas, getSprites().rButtonDifficultEasy, getSprites().gButtonOverlay, getSprites().gButton, Integer.valueOf(cDifficulty.value), GameValues.DIFFICULTY_EASY, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rButtonDifficultMedium, getSprites().gButtonOverlay, getSprites().gButton, Integer.valueOf(cDifficulty.value), GameValues.DIFFICULTY_MEDIUM, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rButtonDifficultHard, getSprites().gButtonOverlay, getSprites().gButton, Integer.valueOf(cDifficulty.value), GameValues.DIFFICULTY_HARD, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rButtonDifficultEasy, getString(R.string.easy));
            drawText(canvas, getSprites().rButtonDifficultMedium, getString(R.string.medium));
            drawText(canvas, getSprites().rButtonDifficultHard, getString(R.string.hard));

            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back));
        } else if (Constants.RenderMode.SCORE == mRenderMode) {

            drawText(canvas, getSprites().rMsgScore, getString(R.string.menubutton_score));

            drawTextUnbounded(canvas, getSprites().rMsgScore1, MessageFormat.format(getString(R.string.message_score), 1), GameValues.cFilterBlue);
            drawTextUnbounded(canvas, getSprites().rMsgScore2, MessageFormat.format(getString(R.string.message_score), 2), GameValues.cFilterBlue);
            drawTextUnbounded(canvas, getSprites().rMsgScore3, MessageFormat.format(getString(R.string.message_score), 3), GameValues.cFilterBlue);
            drawTextUnbounded(canvas, getSprites().rMsgScore4, MessageFormat.format(getString(R.string.message_score), 4), GameValues.cFilterBlue);
            drawTextUnbounded(canvas, getSprites().rMsgScore5, MessageFormat.format(getString(R.string.message_score), 5), GameValues.cFilterBlue);
            drawTextUnbounded(canvas, getSprites().rMsgPointsTotal, getString(R.string.message_points), GameValues.cFilterBlue);
            drawTextUnbounded(canvas, getSprites().rMsgCoinsTotal, getString(R.string.message_coins), GameValues.cFilterBlue);

            drawTextUnboundedScaled(canvas, getSprites().rScore1, Integer.toString(inventory.game1), GameValues.cFilterYellow);
            drawTextUnboundedScaled(canvas, getSprites().rScore2, Integer.toString(inventory.game2), GameValues.cFilterYellow);
            drawTextUnboundedScaled(canvas, getSprites().rScore3, Integer.toString(inventory.game3), GameValues.cFilterYellow);
            drawTextUnboundedScaled(canvas, getSprites().rScore4, Integer.toString(inventory.game4), GameValues.cFilterYellow);
            drawTextUnboundedScaled(canvas, getSprites().rScore5, Integer.toString(inventory.game5), GameValues.cFilterYellow);

            drawTextUnboundedScaled(canvas, getSprites().rPointsTotal, Long.toString(inventory.points), GameValues.cFilterYellow);
            drawTextUnboundedScaled(canvas, getSprites().rCoinsTotal, Long.toString(inventory.coins), GameValues.cFilterYellow);

            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, Constants.ACTION_SCORE, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back));
        } else if (Constants.RenderMode.GAME == mRenderMode) {

            drawText(canvas, getSprites().rMsgWait, getString(R.string.message_loading));
        }

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

        rnd = new Random();

        getSprites().gBackground = loadGraphic(R.drawable.background);
//				GameValues.backgrounds[rnd.nextInt(GameValues.backgrounds.length)], 0, 0);

        getSprites().gLogo = loadGraphic(R.drawable.ic_logo, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER_TOP, 0.10f, 0, 340, getSprites().gLogo);

        // button backgrounds
        getSprites().gButtonMain = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_wide, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_flat, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white);

        getSprites().gTextLogo = loadGraphic(R.drawable.text_logo);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.4f, 0, 0, getSprites().gTextLogo.image.getBounds());

        // navigation and text buttons
        getSprites().rBtnBack = getSprites().gButtonMain.image.copyBounds();
        getSprites().rBtnStart = getSprites().gButtonMain.image.copyBounds();
        getSprites().rBtnScore = getSprites().gButtonMain.image.copyBounds();
        getSprites().rBtnOptions = getSprites().gButtonMain.image.copyBounds();
        getSprites().rMsgWait = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER_TOP, 0.25f, 0, 100, getSprites().rBtnStart);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.25f, 50, 100, getSprites().rBtnOptions);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.25f, 50, 100, getSprites().rBtnScore);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 50, 25, getSprites().rBtnBack);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.80f, 0, 0, getSprites().rMsgWait);

        // Option menu graphics
        getSprites().rMsgDifficulty = getSprites().gButton.image.copyBounds();
        getSprites().rButtonDifficultEasy = getSprites().gButton.image.copyBounds();
        getSprites().rButtonDifficultMedium = getSprites().gButton.image.copyBounds();
        getSprites().rButtonDifficultHard = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.20f, 200, 50, getSprites().rMsgDifficulty);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 130, 250, getSprites().rButtonDifficultEasy);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 130, 500, getSprites().rButtonDifficultMedium);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 130, 750, getSprites().rButtonDifficultHard);

        // Score menu graphics
        getSprites().rMsgScore = getSprites().gButton.image.copyBounds();
        getSprites().rMsgScore1 = getSprites().gButton.image.copyBounds();
        getSprites().rMsgScore2 = getSprites().gButton.image.copyBounds();
        getSprites().rMsgScore3 = getSprites().gButton.image.copyBounds();
        getSprites().rMsgScore4 = getSprites().gButton.image.copyBounds();
        getSprites().rMsgScore5 = getSprites().gButton.image.copyBounds();
        getSprites().rMsgPointsTotal = getSprites().gButton.image.copyBounds();
        getSprites().rMsgCoinsTotal = getSprites().gButton.image.copyBounds();
        getSprites().rScore1 = getSprites().gButton.image.copyBounds();
        getSprites().rScore2 = getSprites().gButton.image.copyBounds();
        getSprites().rScore3 = getSprites().gButton.image.copyBounds();
        getSprites().rScore4 = getSprites().gButton.image.copyBounds();
        getSprites().rScore5 = getSprites().gButton.image.copyBounds();
        getSprites().rPointsTotal = getSprites().gButton.image.copyBounds();
        getSprites().rCoinsTotal = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 230, 25, getSprites().rMsgScore);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 270, 200, getSprites().rMsgScore1);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 270, 300, getSprites().rMsgScore2);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 270, 400, getSprites().rMsgScore3);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 270, 500, getSprites().rMsgScore4);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 270, 600, getSprites().rMsgScore5);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 316, 700, getSprites().rMsgPointsTotal);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 300, 800, getSprites().rMsgCoinsTotal);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 130, 200, getSprites().rScore1);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 130, 300, getSprites().rScore2);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 130, 400, getSprites().rScore3);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 130, 500, getSprites().rScore4);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 130, 600, getSprites().rScore5);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 100, 700, getSprites().rPointsTotal);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 100, 800, getSprites().rCoinsTotal);

        mLayer = new Paint();
        mLayer.setColor(Color.WHITE);
        mLayer.setAlpha(75);
        mLayer.setStyle(Style.FILL_AND_STROKE);
        mLayerBorder = new Paint();
        mLayerBorder.setColor(Color.BLACK);
        mLayerBorder.setAlpha(100);
        mLayerBorder.setStyle(Style.STROKE);

    }

    public synchronized void updateRenderMode(RenderMode renderMode) {
        mRenderMode = renderMode;
    }

    private void drawLayer(Canvas canvas) {
        canvas.drawRect(scaleWidth(175), scaleHeight(10), screenWidth - scaleWidth(5), screenHeight - scaleHeight(10), mLayer);
        canvas.drawRect(scaleWidth(175), scaleHeight(10), screenWidth - scaleWidth(5), screenHeight - scaleHeight(10), mLayerBorder);
    }

    @Override
    public void reset() {
    }

    @Override
    public float getCharSpacing() {
        return Constants.CHAR_SPACING;
    }

}
