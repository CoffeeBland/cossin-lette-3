package com.coffeebland.cossinlette3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.state.SplashState;
import com.coffeebland.cossinlette3.state.State;
import com.coffeebland.cossinlette3.state.StateManager;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.Time;

public class CossinLette3 extends ApplicationAdapter {

	protected long lastNanoTime = -1;
	protected float accumulator = 0;
	protected Class<? extends State> initialState;

	public CossinLette3() {
		initialState = SplashState.class;
	}

	SpriteBatch batch;
	StateManager mgr;
	
	@SuppressWarnings("AccessStaticViaInstance")
	@Override
	public void create () {
		Gdx.graphics.setVSync(true);
		batch = new SpriteBatch();
		batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
		batch.enableBlending();
		mgr = new StateManager(initialState);
	}

	@SuppressWarnings("AccessStaticViaInstance")
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		batch = new SpriteBatch();
		batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
		batch.enableBlending();

		mgr.resize(width, height);
	}

	@Override
	public void render() {
		Color bgCol = mgr.getBackgroundColor();
		Gdx.gl.glClearColor(bgCol.r, bgCol.g, bgCol.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mgr.render(batch);

		// Check for update steps
		long nanoTime = System.nanoTime();
		if (lastNanoTime != -1) {
			float delta = Time.nanoToMillis((nanoTime - lastNanoTime));

			accumulator += delta;

			if (accumulator > 0) {
				mgr.update(Const.TIME_STEP);
			}
			while (accumulator > 0) {
				accumulator -= Const.TIME_STEP;
			}
		}
		lastNanoTime = nanoTime;
	}
}
