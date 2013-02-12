package com.nickpenaranda.devolympics.calibration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.nickpenaranda.devolympics.AbstractScreen;
import com.nickpenaranda.devolympics.DeviceOlympics;
import com.nickpenaranda.devolympics.fitts.FittsScreen;
import com.nickpenaranda.devolympics.miscscreens.TransitionScreen;
import com.nickpenaranda.devolympics.transitions.FadeTransition;

/*
 * INFO FOR LATERS
 * 
 * Target/Quarter image = 420px
 * Quarter dimensions
 * 24.26mm -> .955in
 * 
 * 420px * calSize = .955in
 * 420px * calSize / .955in = N px/in 
 * 
 * retinal display ppi = 326, cap to 350
 * 420 * x / .955 = 350
 */

public class CalibrationScreen extends AbstractScreen {
	static final float MAX_CAL = 0.9095f;
	static final float MIN_CAL = 0.1637f;
	static final float DEFAULT_CAL = 0.4547f; // 200 dpi
	
	BitmapFont font;
	ShapeRenderer renderer;
	TextureAtlas atlas;
	
	Sprite quarter;
	Sprite calArea, calAreaActive;
	Sprite ok, okActive;
	Rectangle okBounding;
	
	TextureRegion texBG;
	float calSize;
	float phase;
	
	int touchAnchor;
	boolean isDragging,okClicked;
	
	public CalibrationScreen(DeviceOlympics game, SpriteBatch batch) {
		super(game, batch);
		
		atlas = new TextureAtlas(Gdx.files.internal("gfx/calibration/pack"));
		quarter = atlas.createSprite("quarter");
		calArea = atlas.createSprite("calArea");
		calAreaActive = atlas.createSprite("calAreaActive");
		
		ok = atlas.createSprite("ok");
		okActive = atlas.createSprite("okActive");
		okBounding = new Rectangle(Gdx.graphics.getWidth() - ok.getWidth(),Gdx.graphics.getHeight() - ok.getHeight(),ok.getWidth(),ok.getHeight());
		
		texBG = atlas.findRegion("bg");
		
		font = new BitmapFont(Gdx.files.internal("font2.fnt"),false);
		renderer = new ShapeRenderer();

		phase = 0;

		touchAnchor = 0;
		isDragging = false;
		calSize = game.getPref().getFloat("scaleCoefficient", DEFAULT_CAL);
		
		quarter.setOrigin(quarter.getWidth() / 2, quarter.getHeight() / 2);
		quarter.setPosition(Gdx.graphics.getWidth() - (quarter.getWidth() * MAX_CAL), 
							   (Gdx.graphics.getHeight() - quarter.getHeight()) / 2);
		
		calArea.setPosition(2f, (Gdx.graphics.getHeight() - calArea.getHeight()) / 2);
		calAreaActive.setPosition(2f, (Gdx.graphics.getHeight() - calArea.getHeight()) / 2);
		
		ok.setPosition(Gdx.graphics.getWidth() - ok.getWidth(), 0);
		okActive.setPosition(Gdx.graphics.getWidth() - ok.getWidth(), 0);
		
		Gdx.input.setInputProcessor(new InputProcessorImpl());
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		
		phase = (float)((phase + 2 * (delta / 1.5f) * Math.PI) % (2 * Math.PI));
		
		float sw = Gdx.graphics.getWidth();
		float sh = Gdx.graphics.getHeight();
		float my = sh / 2;
		float bgw = texBG.getRegionWidth();
		float bgh = texBG.getRegionHeight();
		
		float quarterYBounds = 210 * calSize;
		float coinCenter = sw - (quarter.getWidth() * MAX_CAL) / 2.25f;
		float guideX = coinCenter - (210 * calSize) - 8;

		String calText = String.format("%.1f pixels per inch", 420 * calSize / .955f);

		batch.begin();
			for(float x = 0;x < sw;x += bgw) {
				for(float y = 0;y < sh; y += bgh) {
					batch.draw(texBG, x, y);
				}
			}
			if(okClicked)
				okActive.draw(batch);
			else
				ok.draw(batch);
			
			quarter.setScale(calSize);
			if(isDragging)
				calAreaActive.draw(batch);
			else
				calArea.draw(batch);
			quarter.draw(batch);
			font.setScale(1f);
			font.drawMultiLine(batch, "1. Find a quarter\n2. Drag in the area below until this\n    quarter matches the real thing!", 10, Gdx.graphics.getHeight() - 8);
			font.draw(batch,"0.96in?",guideX - 100,my + font.getCapHeight() + 1);
			font.draw(batch, calText, 10, 10 + font.getCapHeight());
			float calTextScale = (float)(1f + (0.1 * Math.sin(phase)));
			font.setScale(calTextScale);
			font.drawMultiLine(batch, "CALIBRATION", sw-208, sh - 24 + font.getCapHeight(), 200f, HAlignment.CENTER);
		batch.end();
		
		Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        renderer.begin(ShapeType.Line);
			renderer.setColor(1f,1f,1f,0.6f);
			renderer.line(8, 8, 280, 8);
			renderer.line(8, sh - 24, 280, sh - 24);
			renderer.line(8, sh - 56, 432, sh - 56);
			renderer.line(8, sh - 88, 432, sh - 88);
			renderer.line(sw - 208, sh - 26, sw - 8, sh - 26);
			
			renderer.line(guideX, my + quarterYBounds, guideX, my - quarterYBounds);
			renderer.line(guideX - 100, my, guideX, my);
			renderer.line(coinCenter, my + quarterYBounds, guideX, my + quarterYBounds);
			renderer.line(coinCenter, my - quarterYBounds, guideX, my - quarterYBounds);
		renderer.end();
	}

	@Override
	public void dispose() {
		super.dispose();

		atlas.dispose();
		font.dispose();
		renderer.dispose();		
	}
	
	public void doOk() {
		Preferences pref = game.getPref();
		
		pref.putFloat("scaleCoefficient", calSize);
		pref.putBoolean("calibrated", true);
		pref.flush();
		
		game.setScreen(new TransitionScreen(game,this,new FittsScreen(game,batch),new FadeTransition(1.0f, 1.0f)));
	}
	
	class InputProcessorImpl implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if(x <= Gdx.graphics.getWidth() / 2) {
				touchAnchor = y;
				isDragging = true;
			} else if(okBounding.contains(x,y)) {
				okClicked = true;
			}
			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if(okClicked && okBounding.contains(x,y)) {
				// Exit logic
				Gdx.app.log("CalibrationScreen", "OK pressed");
				doOk();
			}
			isDragging = false;
			okClicked = false;
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			if(x > Gdx.graphics.getWidth() / 2)
				return false;
			
			//Gdx.app.log("CalibrationScreen","x = " + x);
			calSize -= ( (float)(y - touchAnchor)/ Gdx.graphics.getHeight() ) / 4;
			if(calSize < MIN_CAL)
				calSize = MIN_CAL;
			else if(calSize > MAX_CAL)
				calSize = MAX_CAL;
			touchAnchor = y;
			return true;
		}

		@Override
		public boolean touchMoved(int x, int y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
