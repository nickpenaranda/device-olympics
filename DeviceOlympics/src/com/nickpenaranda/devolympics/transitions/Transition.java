package com.nickpenaranda.devolympics.transitions;

import com.badlogic.gdx.Screen;

public interface Transition {
	public void render(float t, Screen current, Screen next);
	public boolean isFinished();
}
