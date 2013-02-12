package com.nickpenaranda.devolympics.fitts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nickpenaranda.devolympics.AbstractVisual;

public class TargetVisual extends AbstractVisual {
	private static final String spriteNameActive = "targetActive";
	private static final String spriteNameInactive = "targetInactive";
	private static final float[] alpha = {1f, .25f};
	
	private SpriteBatch mBatch;
	private Sprite mSprite;
	private float mAlpha;
	
	public TargetVisual(TextureAtlas atlas, SpriteBatch batch, boolean active) {
		mBatch = batch;
		mSprite = atlas.createSprite(active ? spriteNameActive : spriteNameInactive);
		mAlpha = alpha[active ? 0 : 1];
		mSprite.setOrigin(0f, 0f);
	}
	
	public void set(float scale, float x, float y) {
		mSprite.setScale(scale);
		mSprite.setPosition(x, y);
	}
	
	@Override
	public void render() {
		mSprite.draw(mBatch,mAlpha);
	}

	@Override
	public void update(float delta) {
		// Empty
	}

	@Override
	public void dispose() {
		// Empty
	}

	@Override
	public boolean isAnimated() {
		return false;
	}

	@Override
	public boolean isDead() {
		return false;
	}

}
