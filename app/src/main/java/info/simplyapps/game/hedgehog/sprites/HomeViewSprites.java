package info.simplyapps.game.hedgehog.sprites;

import android.graphics.Rect;

import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.sprites.ViewSprites;

public class HomeViewSprites implements ViewSprites {

    // main
    public Graphic gBackground;
    public Graphic gLogo;

    // generic buttons
    public Graphic gButton;
    public Graphic gButtonMain;
    public Graphic gButtonOverlay;

    public Graphic gTextLogo;

    // score
    public Rect rMsgScore;
    public Rect rMsgScore1;
    public Rect rMsgScore2;
    public Rect rMsgScore3;
    public Rect rMsgScore4;
    public Rect rMsgScore5;
    public Rect rMsgPointsTotal;
    public Rect rMsgCoinsTotal;
    public Rect rScore1;
    public Rect rScore2;
    public Rect rScore3;
    public Rect rScore4;
    public Rect rScore5;
    public Rect rPointsTotal;
    public Rect rCoinsTotal;

    // options
    public Rect rMsgDifficulty;
    public Rect rButtonDifficultEasy;
    public Rect rButtonDifficultMedium;
    public Rect rButtonDifficultHard;

    // main menu system
    public Rect rBtnStart;
    public Rect rBtnOptions;
    public Rect rBtnScore;
    public Rect rBtnBack;
    public Rect rMsgWait;

    @Override
    public void init() {
    }

    @Override
    public void clean() {
    }

}
