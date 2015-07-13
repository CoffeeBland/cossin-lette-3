package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.Nullable;

public abstract class AutoSwitchState extends State<Integer> {
    protected float remainingTime = getDuration();
    protected boolean hasSwitched = false;

    protected abstract float getDuration();
    protected abstract Class<? extends State> getNextStateClass();
    protected abstract Color getTransitionColor();
    protected abstract float getTransitionDuration();

    @Override
    public boolean shouldBeReused() {
        return false;
    }

    @Override
    public void update(float delta) {
        if (!hasSwitched) {
            if (remainingTime >= 0) {
                remainingTime -= delta;
            } else {
                new StateManager.TransitionArgs<>(getNextStateClass())
                        .setColor(getTransitionColor())
                        .setLength(getTransitionDuration(), getTransitionDuration())
                        .beginSwitch(stateManager);
                hasSwitched = true;
            }
        }
    }

    @Override
    public void onTransitionInStart(boolean firstTransition, @Nullable Integer duration) {
        remainingTime = duration == null ? getDuration() : duration;
        hasSwitched = false;
    }
}
