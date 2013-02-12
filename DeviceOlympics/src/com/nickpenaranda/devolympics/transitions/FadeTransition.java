package com.nickpenaranda.devolympics.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class FadeTransition implements Transition {
	boolean finished;
	float fadeOut, fadeIn;
	float state;
	
	ShapeRenderer renderer;
	
	public FadeTransition(float fadeOut, float fadeIn) {
		this.fadeOut = fadeOut;
		this.fadeIn = fadeIn;
		
		renderer = new ShapeRenderer();
		state = 0;
		finished = false;
	}
	
	@Override
	public void render(float delta, Screen current, Screen next) {
		float sx = Gdx.graphics.getWidth();
		float sy = Gdx.graphics.getHeight();

		state += delta;
		
		if(state < fadeOut) {
			current.render(delta);
			
			Gdx.gl.glEnable(GL10.GL_BLEND);
	        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            
			renderer.begin(ShapeType.FilledRectangle);
				renderer.setColor(0f, 0f, 0f, state / fadeOut);
				renderer.filledRect(0, 0, sx, sy);
			renderer.end();
		} else if(state < fadeOut + fadeIn) {
			next.render(delta);
			
			Gdx.gl.glEnable(GL10.GL_BLEND);
	        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            
			renderer.begin(ShapeType.FilledRectangle);
				renderer.setColor(0f, 0f, 0f, 1 - ((state - fadeOut) / fadeIn));
				renderer.filledRect(0, 0, sx, sy);
			renderer.end();
		} else {
			finished = true;
		}
	}
	
	@Override
	public boolean isFinished() { return(finished); }
}
