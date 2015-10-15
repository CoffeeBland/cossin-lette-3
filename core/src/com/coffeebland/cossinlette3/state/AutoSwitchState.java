package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.graphics.Color;
import com.coffeebland.cossinlette3.utils.event.EventManager;

public abstract class AutoSwitchState extends StateImpl<Void> {

    protected abstract long getDuration();
    protected abstract Class<? extends StateImpl> getNextStateClass();
    protected abstract Color getTransitionColor();
    protected abstract float getTransitionDuration();

    protected EventManager.Entry switchEvent;

    @Override
    public void onTransitionInStart() {
        super.onTransitionInStart();
        switchEvent = eventManager.post(getDuration(), () ->
                        new StateManager.TransitionArgs<>(getNextStateClass())
                                .setColor(getTransitionColor())
                                .setLength(getTransitionDuration(), getTransitionDuration())
                                .beginSwitch(stateManager)
        );
    }
}
