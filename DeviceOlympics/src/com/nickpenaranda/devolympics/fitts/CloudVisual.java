package com.nickpenaranda.devolympics.fitts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.nickpenaranda.devolympics.AbstractVisual;

public class CloudVisual extends AbstractVisual {
	private static final String[] cloudNames = {"cloud1","cloud2","cloud3","cloud4"};
	private static final float[][] cloudPos = {{.16f, .45f}, {.69f,.58f}, {.40f,.55f},{.84f,.67f}};
	private static final float cloudPhase[] = {(float)Math.PI, (float)Math.PI / 3f, (float)Math.PI / 5f, (float)Math.PI + (float)Math.PI / 7f};
	private static final float cloudPeriod[] = {83f, 51f, 31f, 101f};

	private static final int maxClouds = 4;
	private static int cloudIndex = 0;
	
	private SpriteBatch mBatch;
	private Sprite mSprite;
	private float mPhase;
	private float mPeriod;
	
	/*
	 * Need to fix this dependency on an atlas...but how???
	 */
	public CloudVisual(TextureAtlas atlas, SpriteBatch batch, float scale) {
		if(cloudIndex >= maxClouds) {
			throw new GdxRuntimeException("Too many clouds! maxClouds = " + maxClouds);
		}
		
		mSprite = atlas.createSprite(cloudNames[cloudIndex]);
		mSprite.setPosition(cloudPos[cloudIndex][0] * Gdx.graphics.getWidth(),cloudPos[cloudIndex][1] * Gdx.graphics.getHeight());
		mSprite.setScale(scale);
		mPhase = cloudPhase[cloudIndex];
		mPeriod = cloudPeriod[cloudIndex];
		mBatch = batch;
		
		cloudIndex++;
	}
	
	@Override
	public void render() {
		mSprite.draw(mBatch);
	}

	@Override
	public void update(float delta) {
		mPhase = (mPhase + delta * 2f * (float)Math.PI / mPeriod) % mPeriod;
		mSprite.translate((float)MathUtils.sin(mPhase) / 20f, 0f);
	}

	@Override
	public void dispose() {
		// Nothing to do here
	}

	@Override
	public boolean isAnimated() {
		return true;
	}

	@Override
	public boolean isDead() {
		return false;
	}
}
