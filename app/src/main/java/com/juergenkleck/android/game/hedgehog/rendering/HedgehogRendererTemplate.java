package com.juergenkleck.android.game.hedgehog.rendering;

import android.content.Context;

import java.util.Properties;

import com.juergenkleck.android.gameengine.rendering.GenericRendererTemplate;

/**
 * Android app - Hedgehog
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public abstract class HedgehogRendererTemplate extends GenericRendererTemplate {

    public HedgehogRendererTemplate(Context context, Properties properties) {
        super(context, properties);
    }

    public boolean logEnabled() {
        return false;
    }

}
