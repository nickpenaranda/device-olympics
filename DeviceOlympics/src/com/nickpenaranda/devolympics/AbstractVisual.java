package com.nickpenaranda.devolympics;

public abstract class AbstractVisual {
	/**
	 * Draw this visual to the screen
	 */
	public abstract void render();
	
	/**
	 * Called at the beginning of each render()
	 */
	public abstract void update(float delta);
	
	/**
	 * Should be called when the screen is disposed.  Use to release resources
	 */
	public abstract void dispose();
	
	/**
	 * Should return true if inheriting object requires calls to update
	 * @return  true if this object requires calls to update(...)
	 */
	public abstract boolean isAnimated();
	
	/**
	 * Signals when this visual should be removed from render list
	 * @return  true when this visual should be removed from render list
	 */
	public abstract boolean isDead();
}
