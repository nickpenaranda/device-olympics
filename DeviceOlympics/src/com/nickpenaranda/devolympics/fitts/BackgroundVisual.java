package com.nickpenaranda.devolympics.fitts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.nickpenaranda.devolympics.AbstractVisual;

public class BackgroundVisual extends AbstractVisual {
	private static final Color skyTop = new Color(143/255f, 248/255f, 255/255f, 1f);
	private static final Color skyBottom = new Color(255/255f, 193/255f, 123/255f, 1f);
	private static final String spriteName = "bgDetail";
	
	private ShapeRenderer mRenderer;
	private SpriteBatch mBatch;
	private Sprite mBGDetail;
	private float w,h;
	
	public BackgroundVisual(TextureAtlas atlas, ShapeRenderer renderer, SpriteBatch batch) {
		mRenderer = renderer;
		mBatch = batch;
		
		mBGDetail = atlas.createSprite(spriteName);
		mBGDetail.setOrigin(0f, 0f);
		mBGDetail.setPosition(0f, 0f);
		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		mBGDetail.setScale(w / mBGDetail.getWidth()); // @HACK
	}
	
	@Override
	public void render() {
		mBatch.end();
		
		mRenderer.begin(ShapeType.FilledRectangle);
			mRenderer.filledRect(0f, 0f, w, h, skyBottom, skyBottom, skyTop, skyTop);
		mRenderer.end();

		mBatch.begin();
			mBGDetail.draw(mBatch);
	}

	@Override
	public void update(float delta) {
		// Empty
	}

	@Override
	public void dispose() {
		// Nothing to do here
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
