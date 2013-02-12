package com.nickpenaranda.devolympics.miscscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nickpenaranda.devolympics.AbstractScreen;
import com.nickpenaranda.devolympics.DeviceOlympics;
import com.nickpenaranda.devolympics.calibration.CalibrationScreen;
import com.nickpenaranda.devolympics.fitts.FittsScreen;
import com.nickpenaranda.devolympics.transitions.FadeTransition;

public class SplashScreen extends AbstractScreen implements Screen {
	public static final float DUR_SPLASH = 3f;
	
	TextureAtlas atlas;
	Sprite texSplash;
	
	float lifetime;
	boolean transitionStarted;
	
	public SplashScreen(DeviceOlympics game, SpriteBatch batch) {
		super(game, batch);

		atlas = new TextureAtlas(Gdx.files.internal("gfx/splash/pack"));
		texSplash = atlas.createSprite("splash");
		
		lifetime = 0;
		transitionStarted = false;
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		texSplash.setBounds(w / 8, h / 8, 3 * w / 4, 3 * h / 4);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		lifetime += delta;
		if(lifetime > DUR_SPLASH && !transitionStarted) {
			Screen nextScreen = !game.getPref().getBoolean("calibrated",false)
					? new CalibrationScreen(game,batch)
					: new FittsScreen(game, batch);
					
			game.setScreen(new TransitionScreen(game,this,nextScreen,new FadeTransition(1.5f,1.0f)));
			transitionStarted = true;
		}
		
		batch.begin();
			texSplash.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose() {
		atlas.dispose();
	}
}
