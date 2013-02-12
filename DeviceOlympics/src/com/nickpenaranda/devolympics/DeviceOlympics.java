package com.nickpenaranda.devolympics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nickpenaranda.devolympics.miscscreens.SplashScreen;

public class DeviceOlympics extends Game {
	private static final float targetGraphicsWidth = 800f;
	
	Preferences pref;
	SpriteBatch batch;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		pref = Gdx.app.getPreferences("preferences");
		setScreen(new SplashScreen(this, batch));
	}
	
	public Preferences getPref() { return(pref); }
	
	public float getGraphicsScale() {
		return(Gdx.graphics.getWidth() / targetGraphicsWidth);
	}
}
