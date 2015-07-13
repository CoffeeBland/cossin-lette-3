package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StateManager {
    public <A, S extends State<A>> StateManager(Class<S> initialState) {
        new TransitionArgs<>(initialState)
                .setArgs(null)
                .setLength(State.TRANSITION_LONG, State.TRANSITION_LONG)
                .setColor(Color.BLACK.cpy())
                .beginSwitch(this);
    }

    protected final Map<String, State<?>> states = new HashMap<>();
    @Nullable
    protected State currentState;
    @Nullable
    protected TransitionState<?, ?> transitionState;

    public Color getBackgroundColor() {
        if (currentState != null) return currentState.getBackgroundColor();
        else return Color.BLACK.cpy();
    }

    public <A, S extends State<A>> void switchToState(TransitionArgs<A, S> transitionArgs) {
        transitionState = new TransitionState<>(transitionArgs);
    }

    public void render(SpriteBatch batch) {
        if (currentState != null) currentState.render(batch);
        if (transitionState != null) transitionState.render(batch);
    }

    public void update(float delta) {
        if (transitionState != null) transitionState.update(delta);
        if (currentState != null) currentState.update(delta);
    }
    public void resize(int width, int height) {
        if (currentState != null) currentState.resize(width, height);
    }

    public static class TransitionArgs<A, S extends State<A>> {
        public float outLength, inLength;
        @NotNull
        public Class<S> stateType;
        @Nullable
        public A args;
        @Nullable
        public Color color;

        public TransitionArgs(@NotNull Class<S> stateType) {
            this.stateType = stateType;
        }
        public TransitionArgs(TransitionArgs<A, S> tArgs) {
            outLength = tArgs.outLength;
            inLength = tArgs.inLength;
            stateType = tArgs.stateType;
            args = tArgs.args;
            color = (tArgs.color == null ? Color.BLACK : tArgs.color).cpy();
        }

        public TransitionArgs<A, S> setLength(float out, float in) {
            outLength = out;
            inLength = in;
            return this;
        }

        public TransitionArgs<A, S> setArgs(@Nullable A args) {
            this.args = args;
            return this;
        }

        public TransitionArgs<A, S> setColor(@Nullable Color color) {
            this.color = color;
            return this;
        }

        public void beginSwitch(StateManager stateManager) {
            stateManager.switchToState(this);
        }
    }
    public class TransitionState<A, S extends State<A>> extends TransitionArgs<A, S> {
        public float remainingOutLength, remainingInLength;
        public boolean hasSwitched = false;
        @NotNull
        public S nextState;
        public boolean isNewState;

        public TransitionState(TransitionArgs<A, S> args) {
            super(args);
            remainingOutLength = args.outLength;
            remainingInLength = args.inLength;

            // Get state
            if (states.containsKey(stateType.getName())) {
                // We assume that the cachedState must be of the proper type: we assume there are no name
                // collisions
                //noinspection unchecked
                nextState = (S)states.get(stateType.getName());
                isNewState = false;
            } else {
                try {
                    nextState = stateType.newInstance();
                    nextState.setStateManager(StateManager.this);
                    if (nextState.shouldBeReused()) {
                        states.put(stateType.getName(), nextState);
                    }
                    isNewState = true;
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException("The state could not be instantiated");
                }
            }

            Gdx.input.setInputProcessor(null);

            // Notify state (if there is none then we will directly animate the fade in)
            if (currentState != null) currentState.onTransitionOutStart();
            else {
                remainingOutLength = 0;
                switchStates();
            }
        }

        public void render(SpriteBatch batch) {
            assert color != null;
            if (hasSwitched) {
                // Finishing transition
                color.a = remainingInLength / inLength;
            } else {
                // Beginning transition
                color.a = 1 - remainingOutLength / outLength;
            }
            batch.begin();
            batch.setColor(color);
            batch.draw(Textures.WHITE_PIXEL, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
            batch.end();
        }
        public void update(float delta) {
            if (hasSwitched) {
                // Finishing transition
                remainingInLength -= delta;
                if (remainingInLength <= 0) {
                    if (currentState != null) {
                        currentState.onTransitionInFinish();
                    }
                    if (transitionState == this) transitionState = null;
                }
            } else {
                // Beginning transition
                remainingOutLength -= delta;
                if (remainingOutLength <= 0) switchStates();
            }
        }
        protected void switchStates() {
            if (currentState != null) currentState.onTransitionOutFinish();
            nextState.onTransitionInStart(isNewState, args);
            Gdx.input.setInputProcessor(nextState.getInputProcessor());
            currentState = nextState;
            hasSwitched = true;
            update(-remainingOutLength);
        }
    }
}
