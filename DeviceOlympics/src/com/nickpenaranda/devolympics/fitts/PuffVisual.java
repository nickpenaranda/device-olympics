package com.nickpenaranda.devolympics.fitts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.nickpenaranda.devolympics.AbstractVisual;

public class PuffVisual extends AbstractVisual {
	private static final int spriteSize = 64;
	
	private static final float[] velRange = {40f, 40f};
	private static final float[] lifeRange = {.3f, .8f};
	private static final float[] scaleRange = {2f, 6f};
	
	private static final String spriteName = "puff";
	private static Sprite sprite;
	private static SpriteBatch batch;
	private static float unitToInch;
	
	private float mPos[];
	private float mVel[];
	private float mLife;
	private float mScale;
	private float mOffset;
	private float mAlpha;
	
	public PuffVisual(float x, float y) {
		mPos = new float[] {x, y};
		mVel = new float[] {MathUtils.random(-velRange[0], velRange[0]), MathUtils.random(-velRange[1], velRange[1])};
		
		this.mLife = MathUtils.random(lifeRange[0], lifeRange[1]);
		this.mScale = MathUtils.random(scaleRange[0], scaleRange[1]) * unitToInch;
		this.mOffset = spriteSize * (mScale / 2); 
	}
	
	public static void init(TextureAtlas atlas, SpriteBatch batch, float unitToInch) {
		PuffVisual.batch = batch;
		PuffVisual.unitToInch = unitToInch; 
		sprite = atlas.createSprite(spriteName);
	}
	
	public boolean isDead() {
		return(mLife < 0f);
	}
	
	@Override
	public void update(float delta) {
		mPos[0] += mVel[0] * delta;
		mPos[1] += mVel[1] * delta;
		mLife -= delta;
		mAlpha = mLife / .8f;
	}

	@Override
	public void render() {
		if(isDead()) return;
		
		sprite.setScale(mScale);
		sprite.setPosition(mPos[0] - mOffset, mPos[1] - mOffset);
		sprite.draw(batch,mAlpha);
	}

	@Override
	public void dispose() {
		// Empty
	}

	@Override
	public boolean isAnimated() {
		return true;
	}
	

}
