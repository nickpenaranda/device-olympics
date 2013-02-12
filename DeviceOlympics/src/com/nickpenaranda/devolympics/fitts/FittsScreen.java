package com.nickpenaranda.devolympics.fitts;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nickpenaranda.devolympics.AbstractScreen;
import com.nickpenaranda.devolympics.AbstractVisual;
import com.nickpenaranda.devolympics.DeviceOlympics;

public class FittsScreen extends AbstractScreen {
	private static final float maxRoundLength = 180f;
	private static final float interRoundInterval = 5f;
	
	private static final float minTargetSize = 0.25f; // In inches
	private static final float maxTargetSize = 1f;
	private static final float minDistance = 1f;
	
	/*
	 * SCORING!!$
	 * The max number of points you can get for a target is <baseTargetValue>
	 * (before multipliers).  Player receives less the slower they are.
	 * They receive full point value if they press target within 
	 * <minTargetValue> * <ID>, where <ID> is the index of difficulty for 
	 * that target (see generateTargetInfo()) and minimum point value beyond 
	 * <minPointsTime> * <ID>.  Responses between <fullPointsTime> and 
	 * <minPointsTime> are scaled exponentially.
	 */
	
	private static final int baseTargetValue = 100;
	private static final float fullPointsTime = .2f;
	private static final float minPointsTime = 1.2f;
	private static final float timeRange = minPointsTime - fullPointsTime;
	private static final float pointScaleFactor = 10f;
	private static final float IDScaleFactor = 0.2f;

	//private static final int targetsPerRound = 200;
	private static final int targetsPerRound = 100;
	private static final int targetsPerMultiplierLevel = 10;
	private static final int maxMultiplierLevel = 10;
	
	private float w,h;
	private float unitToInch;
	
	private TextureAtlas atlas;
	private TargetVisual targetVisual, nextTargetVisual;
	private MultiplierVisual multVisual;
	Rectangle multVisualBounds;
	
	private float timeToTransition;
	private float timer, lastTimer;
	private int currentRound, targetIndex, points, streak, multiplier;
	
	BitmapFont font;
	ShapeRenderer renderer;
	
	private enum State { ENTRY, STARTROUND, PLAYING, ENDROUND }
	State gameState, nextState;
	
	ArrayList<AbstractVisual> bgVisuals, fgVisuals;
	ArrayList<Target> targets;
	private float[] IDs;
	
	Rectangle screenRect;
	
	Sound hit, miss, levelUp;
	
	public FittsScreen(DeviceOlympics game, SpriteBatch batch) {
		super(game, batch);
		
		atlas = new TextureAtlas(Gdx.files.internal("gfx/fitts/pack"));
		renderer = new ShapeRenderer();
		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		screenRect = new Rectangle(0f,0f,w,h);
		
		unitToInch = game.getPref().getFloat("scaleCoefficient") / .955f;
		
		targetVisual = new TargetVisual(atlas, batch, true);
		nextTargetVisual = new TargetVisual(atlas, batch, false);
		PuffVisual.init(atlas, batch, unitToInch);
		
		multVisual = new MultiplierVisual(atlas, batch,game.getGraphicsScale());
		multVisualBounds = multVisual.getBoundingRect();
		
		bgVisuals = new ArrayList<AbstractVisual>();
		fgVisuals = new ArrayList<AbstractVisual>();
		
		bgVisuals.add(new BackgroundVisual(atlas,renderer,batch));
		for(int i=0;i<4;i++) 
			bgVisuals.add(new CloudVisual(atlas,batch,game.getGraphicsScale()));
		
		bgVisuals.add(multVisual);
		
		font = new BitmapFont(Gdx.files.internal("font.fnt"),false);
		
		targetIndex = 0;
		points = 0;
		streak = 0;
		multiplier = 1;
		
		setState(State.ENTRY);
	
		hit = Gdx.audio.newSound(Gdx.files.internal("sound/hit3.wav"));
		miss = Gdx.audio.newSound(Gdx.files.internal("sound/miss2.wav"));
		levelUp = Gdx.audio.newSound(Gdx.files.internal("sound/levelUp.wav"));
		Gdx.input.setInputProcessor(new InputProcessorImpl());
	}

	private void generateTargetInfo() {
		{ // Generate target locations
			float minPixelDistance = 420 * minDistance * unitToInch;

			targets = new ArrayList<Target>(targetsPerRound);
			float targetSize = MathUtils.random(minTargetSize,maxTargetSize) * unitToInch;
			float halfTargetPixelSize = 210 * targetSize;
			Target t = new Target(
					MathUtils.random(halfTargetPixelSize,w-halfTargetPixelSize),
					MathUtils.random(halfTargetPixelSize,h-halfTargetPixelSize),
					targetSize);
			
			targets.add(t);
			for(int i=1;i<targetsPerRound;i++) {
				Target lastTarget = targets.get(i-1);
				Target candidate = null;
				do {
					targetSize = MathUtils.random(minTargetSize,maxTargetSize) * unitToInch;
					halfTargetPixelSize = 210 * targetSize;
					candidate = new Target(
							MathUtils.random(halfTargetPixelSize,w-halfTargetPixelSize),
							MathUtils.random(halfTargetPixelSize,h-halfTargetPixelSize),
							targetSize);
				} while(targetDistance(lastTarget,candidate) < minPixelDistance || multVisualBounds.contains(candidate.x,candidate.y));
				targets.add(candidate);
			}
		}
		
		{ // Calculated associated IDs for each target w/r/t previous target
			IDs = new float[targetsPerRound];
			IDs[0] = 1;
			for(int i=1;i < targets.size(); i++) {
				float distance = targetDistance(targets.get(i-1),targets.get(i));
				IDs[i] = (float)(Math.log((distance / (targets.get(i).size * 420)) + 1) / Math.log(2)); 
			}
		}
	}

	private class Target {
		float x, y, size;
		
		public Target(float x,float y,float size) {
			this.x = x;
			this.y = y;
			this.size = size;
		}
	}
	
	private void setState(State state) {
		gameState = state;
		switch(state) {
		case ENTRY:
			generateTargetInfo();
			nextState = State.STARTROUND;
			timeToTransition = 6.0f;
			break;
		case STARTROUND:
			currentRound++;
			
			generateTargetInfo();
			timer = 0;
			lastTimer = 0;
			
			fgVisuals.add(new RoundTextVisual(atlas,batch,game.getGraphicsScale(),currentRound));
			
			nextState = State.PLAYING;
			timeToTransition = RoundTextVisual.getLifetime() + 2.5f;
			break;
		case PLAYING:
			targetIndex = 0;
			fgVisuals.add(nextTargetVisual);
			fgVisuals.add(targetVisual);
			
			nextState = State.ENDROUND;
			timeToTransition = maxRoundLength ;
			break;
		case ENDROUND:
			fgVisuals.remove(nextTargetVisual);
			fgVisuals.remove(targetVisual);

			nextState = State.STARTROUND;
			timeToTransition = interRoundInterval;
			break;
		}
	}
	
	private void updateLogic(float delta) {
		if(timeToTransition > 0)
			timeToTransition -= delta;
		else
			setState(nextState);
		
		if(gameState == State.PLAYING){
			timer += delta;
		}
		//		switch(gameState) {
//		case ENTRY: // Screen was just created, upon time expiration, show round #
//			break;
//		case STARTROUND: // Showing the round info text
//			break;
//		case PLAYING: // Round info text is fully displayed.  upon time expiration, show ready/set/go
//			timer += delta;
//			break;
//		case ENDROUND:
//			break;
//		}
	}
	
	private float targetDistance(Target a, Target b) {
		return(distanceToTarget(a,b.x,b.y));
	}
	
	private float distanceToTarget(Target t,float x,float y) {
		float dist[] = {t.x - x, t.y - y};
		return((float)Math.sqrt(dist[0] * dist[0] + dist[1] * dist[1]));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void render(float delta) {
		updateLogic(delta);
		super.render(delta);

		if(targetIndex < targetsPerRound - 1) {
			Target nextTarget = targets.get(targetIndex + 1);
			nextTargetVisual.set(nextTarget.size, nextTarget.x - (512 * nextTarget.size / 2), nextTarget.y - (512 * nextTarget.size / 2));
		} else {
			fgVisuals.remove(nextTargetVisual);
		}
		Target currentTarget = targets.get(targetIndex);
		targetVisual.set(currentTarget.size,currentTarget.x - (512 * currentTarget.size / 2), currentTarget.y - (512 * currentTarget.size / 2));
		
		batch.begin();
		batch.enableBlending();
			for(AbstractVisual v : (ArrayList<AbstractVisual>)bgVisuals.clone()) {
				if(v.isAnimated()) {
					v.update(delta);
					if(v.isDead())
						bgVisuals.remove(v);
				}
				v.render();
			}

			for(AbstractVisual v : (ArrayList<AbstractVisual>)fgVisuals.clone()) {
				if(v.isAnimated()) {
					v.update(delta);
					if(v.isDead())
						fgVisuals.remove(v);
				}
				v.render();
			}
			
			font.draw(batch,String.format("%02.0f:%02.0f:%03.0f", Math.floor(timer / 60), Math.floor(timer % 60), timer % 1 * 1000),8,h - 8);
			font.drawWrapped(batch, "" + points, 8, h - 8, w - 16, HAlignment.RIGHT);
		batch.end();
	}
	
	private void setMultiplier(int m) {
		multiplier = m;
		multVisual.set(m);
	}

	@Override
	public void dispose() {
		for(AbstractVisual v : fgVisuals)
			v.dispose();
		for(AbstractVisual v : bgVisuals)
			v.dispose();
		
		renderer.dispose();
		atlas.dispose();
		font.dispose();
	}
	
	private int calculatePoints(float time, float ID) {
		float scaledID = ID * IDScaleFactor;
		float clampedTime = MathUtils.clamp(time, fullPointsTime * scaledID, minPointsTime * scaledID);
		float normedTime = (clampedTime - (fullPointsTime * scaledID)) / (timeRange * scaledID);
		float timeCoef = (float)Math.pow(pointScaleFactor, -normedTime);
		int points = (int)Math.round(baseTargetValue * ID * timeCoef);
		String message = String.format(
				"\n" +
				" time: %.3f\t   ID: %.3f\n" +
				"oTime: %.3f\tmTime: %.3f\n" +
				"cTime: %.3f\tnTime: %.3f\n" +
				"  pts: %d\n", 
				time,ID,
				(clampedTime - (fullPointsTime * scaledID)),(timeRange * scaledID),
				clampedTime,normedTime,points);
		
		Gdx.app.log("FittsScreen", message);
		//Gdx.app.log("FittsScreen", "time = " + clampedTime + "\npoints = " + points);
		return points; 
	}

	class InputProcessorImpl implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if(gameState != State.PLAYING) return false;
			
			Target t = targets.get(targetIndex);
			float dist = distanceToTarget(t,x,h - y);
			//Gdx.app.log("FittsScreen","dist = " + dist);
			if(dist < t.size * 210) {
				// Hit logic
				hit.play();
				if((++streak % targetsPerMultiplierLevel == 0) && (multiplier < maxMultiplierLevel)) {
					setMultiplier(++multiplier);
					levelUp.play();
					// Spawn annunciation...?
				} 
				
				Gdx.app.log("FittsScreen","ID = " + IDs[targetIndex]);
				points += calculatePoints(timer - lastTimer,IDs[targetIndex]);
				lastTimer = timer;
				
				for(int i=0;i<MathUtils.random(5,7);i++)
					bgVisuals.add(new PuffVisual(t.x,t.y));
				if(targetIndex < targetsPerRound - 1) targetIndex++;
				else setState(State.ENDROUND);
			} else {
				miss.play();
				streak = 0;
				setMultiplier(1);
				// Miss logic
			}
			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			return false;
		}

		@Override
		public boolean touchMoved(int x, int y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
