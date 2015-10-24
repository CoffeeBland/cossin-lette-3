package com.coffeebland.cossinlette3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.coffeebland.cossinlette3.state.StateManager;
import com.coffeebland.cossinlette3.state.StateManager.TransitionArgs;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.Time;

public class CossinLette3 extends ApplicationAdapter {

	protected long lastNanoTime = -1;
	protected float accumulator = 0;
	protected TransitionArgs initialArgs;

	public CossinLette3(TransitionArgs initialArgs) {
		this.initialArgs = initialArgs;
	}

	StateManager mgr;

	@Override
	public void create () {
		Gdx.graphics.setVSync(true);
		mgr = new StateManager(initialArgs);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		mgr.resize(width, height);
	}

	@Override
	public void render() {
		Color bgCol = mgr.getBackgroundColor();
		Gdx.gl.glClearColor(bgCol.r, bgCol.g, bgCol.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mgr.render();

		// Check for updateInput steps
		long nanoTime = System.nanoTime();
		if (lastNanoTime != -1) {
			float delta = Time.nanoToMillis((nanoTime - lastNanoTime));

			accumulator += delta;

			if (accumulator > 0) mgr.update(Const.TIME_STEP);
			while (accumulator > 0) accumulator -= Const.TIME_STEP;
		}
		lastNanoTime = nanoTime;
	}
}
