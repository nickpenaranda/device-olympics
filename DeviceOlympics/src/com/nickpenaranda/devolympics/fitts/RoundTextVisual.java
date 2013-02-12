package com.nickpenaranda.devolympics.fitts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.nickpenaranda.devolympics.AbstractVisual;

public class RoundTextVisual extends AbstractVisual {
	private static final String fontName = "gfx/fitts/fontRoundText.fnt", fontImgName = "fontRoundText";
	private static final String fontLargeName = "gfx/fitts/fontRoundNumber.fnt", fontLargeImgName = "fontRoundNumber";
	private static final String roundText = "ROUND";
	private static final float typingInterval = 0.4f; // Time to wait between characters of roundText
	private static final float numberInterval = 0.8f; // Time to wait after last character of roundText and before number
	private static final float numberHold = 1.5f; // Time to hold number before kill
	
	private SpriteBatch mBatch;
	private BitmapFont mFont,mFontLarge;
	private float timeToAction;
	private int charIndex, mRound;
	private boolean showNumber, dead;
	private float my, w;
	
	Sound typewriter;
	
	public RoundTextVisual(TextureAtlas atlas, SpriteBatch batch, float scale, int round) {
		mFont = new BitmapFont(Gdx.files.internal(fontName),atlas.findRegion(fontImgName),false);
		mFont.setScale(scale);
		
		mFontLarge = new BitmapFont(Gdx.files.internal(fontLargeName),atlas.findRegion(fontLargeImgName),false);
		mFontLarge.setScale(scale);
		
		mBatch = batch;
		
		mRound = round;
		
		my = Gdx.graphics.getHeight() / 2;
		w = Gdx.graphics.getWidth();
		
		typewriter = Gdx.audio.newSound(Gdx.files.internal("sound/typewriter.wav"));
		
		timeToAction = 0;
		charIndex = 0;
		showNumber = false;
		dead = false;
	}
	
	@Override
	public void render() {
		String text = roundText.substring(0,charIndex);
		mFont.drawMultiLine(mBatch, text, 0, my + my / 5, w, HAlignment.CENTER);
		if(showNumber) {
			mFontLarge.drawMultiLine(mBatch, "" + mRound, 0, my + my / 5 - mFont.getLineHeight(), w, HAlignment.CENTER);
		}
	}

	@Override
	public void update(float delta) {
		timeToAction -= delta;
		if(timeToAction < 0) {
			if(charIndex < 5) {
				charIndex++;
				typewriter.play();
				timeToAction = charIndex == 5 ? numberInterval : typingInterval;
			} else if(!showNumber) {
				showNumber = true;
				typewriter.play();
				timeToAction = numberHold;
			} else {
				dead = true;
			}
		}
	}

	@Override
	public void dispose() {
		mFont.dispose();
		mFontLarge.dispose();
	}

	@Override
	public boolean isAnimated() {
		return true;
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	public static float getLifetime() {
		return((typingInterval * roundText.length()) + numberInterval + numberHold);
	}

}
