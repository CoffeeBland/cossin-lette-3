package com.coffeebland.cossinlette3.anim;

import org.jetbrains.annotations.NotNull;

public abstract class Animation {
    @NotNull
    protected Interpolator interpolator = (float time) -> time;
    protected float duration, remaining;

    public Animation() {
        duration = remaining = 1000;
    }

    public Animation setDuration(float duration) {
        this.duration = remaining = duration;
        return this;
    }
    public Animation setInterpolator(@NotNull Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public boolean isFinished() {
        return remaining <= 0;
    }

    public void update(float delta) {
        remaining = Math.max(0, remaining - delta);
        apply(remaining / duration);
    }
    public void apply(float time) {
        applyInterpolated(interpolator.getValue(time));
    }

    public abstract void applyInterpolated(float interpolated);

    public interface Interpolator {
        float getValue(float time);
    }
}
