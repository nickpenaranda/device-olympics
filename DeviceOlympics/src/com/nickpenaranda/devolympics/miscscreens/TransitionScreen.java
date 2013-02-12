package com.nickpenaranda.devolympics.miscscreens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nickpenaranda.devolympics.DeviceOlympics;
import com.nickpenaranda.devolympics.transitions.Transition;

public class TransitionScreen implements Screen {
	DeviceOlympics game;
	Screen current, next;
	Transition transition;
	
	ShapeRenderer shapeRenderer;
	Color color;

	public TransitionScreen(DeviceOlympics game, Screen current, Screen next, Transition transition) {
		this.game = game;
		this.current = current;
		this.next = next;
		this.transition = transition;
	}
	
	@Override
	public void render(float delta) {
		transition.render(delta, current, next);
		if(transition.isFinished()) {
			game.setScreen(next);
			current.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	} 
}
