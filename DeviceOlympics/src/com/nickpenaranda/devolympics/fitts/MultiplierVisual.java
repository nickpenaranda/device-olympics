package com.nickpenaranda.devolympics.fitts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.nickpenaranda.devolympics.AbstractVisual;

public class MultiplierVisual extends AbstractVisual {
	public static final float pulseDuration = .2f;
	public static final float pulseScale = 1.2f;
	
	public static final String signSpriteName = "signbase";
	public static final String[] multSpriteNames = {
		"mult1", "mult2", "mult3", "mult4", "mult5",
		"mult6", "mult7", "mult8", "mult9", "mult10"		
	};
	
	private SpriteBatch mBatch;
	private Sprite[] multipliers;
	private Sprite sign;
	private int multiplier;
	private float pulseTimer;
	private float scale;
	
	public MultiplierVisual(TextureAtlas atlas, SpriteBatch batch, float scale) {
		multipliers = new Sprite[multSpriteNames.length];
		for(int i=0;i<multSpriteNames.length;i++) {
			multipliers[i] = atlas.createSprite(multSpriteNames[i]);
			multipliers[i].setOrigin(0f, 0f);
		}
		
		sign = atlas.createSprite(signSpriteName);
		sign.setOrigin(0f, 0f);
		sign.setScale(scale);
		this.scale = scale;
		
		mBatch = batch;
		
		multiplier = 1;
	}
	
	public void set(int m) {
		multiplier = m;
		pulseTimer = pulseDuration;
	}
	
	@Override
	public void render() {
		sign.setPosition(0f, 0f);
		sign.draw(mBatch);
		
		Sprite m = multipliers[multiplier - 1];
		float scaleAdjust = (((pulseScale - 1) * (pulseTimer / pulseDuration)));
		m.setPosition((17f - (17f * scaleAdjust)) * scale, (46f - (46f * scaleAdjust)) * scale);
		m.setScale((1 + scaleAdjust) * scale); 
		m.draw(mBatch);
	}

	@Override
	public void update(float delta) {
		if(pulseTimer > 0)
			pulseTimer -= delta;
		else
			pulseTimer = 0;
	}

	@Override
	public void dispose() {
		// Empty
	}

	@Override
	public boolean isAnimated() {
		return true;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	public Rectangle getBoundingRect() {
		return new Rectangle(0,0,sign.getWidth() * scale,sign.getHeight() * scale);
	}

}
