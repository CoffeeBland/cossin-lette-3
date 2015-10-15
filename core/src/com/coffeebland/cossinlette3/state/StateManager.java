package com.coffeebland.cossinlette3.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coffeebland.cossinlette3.utils.Textures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StateManager {

    @SuppressWarnings("AccessStaticViaInstance")
    public <A, S extends State<A>> StateManager(
            @NotNull TransitionArgs<A, S> args
    ) {
        batch = new SpriteBatch();
        batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        batch.enableBlending();
        args.beginSwitch(this);
    }

    protected final Map<String, State<?>> states = new HashMap<>();
    @Nullable protected State currentState;
    @Nullable protected TransitionState<?, ?> transitionState;
    @NotNull protected Batch batch;

    public Color getBackgroundColor() {
        if (currentState != null) return currentState.getBackgroundColor();
        else return Color.BLACK.cpy();
    }

    public <A, S extends State<A>> void switchToState(TransitionArgs<A, S> transitionArgs) {
        transitionState = new TransitionState<>(transitionArgs);
    }

    public void render() {
        if (currentState != null) currentState.render(batch);
        if (transitionState != null) transitionState.render();
    }

    public void update(float delta) {
        if (transitionState != null) transitionState.update(delta);
        if (currentState != null) currentState.update(delta);
    }
    @SuppressWarnings("AccessStaticViaInstance")
    public void resize(int width, int height) {
        batch = new SpriteBatch();
        batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        batch.enableBlending();

        if (currentState != null) currentState.resize(width, height);
    }

    public static class TransitionArgs<A, S extends State<A>> {
        public float outLength, inLength;
        @NotNull public Class<S> stateType;
        @Nullable public A args;
        @NotNull public Color color;

        public TransitionArgs(@NotNull Class<S> stateType) {
            this.stateType = stateType;
            color = Color.BLACK;
        }
        public TransitionArgs(TransitionArgs<A, S> tArgs) {
            outLength = tArgs.outLength;
            inLength = tArgs.inLength;
            stateType = tArgs.stateType;
            args = tArgs.args;
            color = tArgs.color.cpy();
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
        public TransitionArgs<A, S> setColor(@NotNull Color color) {
            this.color = color;
            return this;
        }

        public void beginSwitch(StateManager stateManager) {
            stateManager.switchToState(this);
        }
    }
    public class TransitionState<A, S extends State<A>> extends TransitionArgs<A, S> {
        public float remainingOutLength, remainingInLength;
        public boolean hasSwitched = false, prepared;
        @NotNull public S nextState;
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
                prepared = true;
            } else {
                try {
                    nextState = stateType.newInstance();
                    nextState.setStateManager(StateManager.this);
                    if (nextState.shouldBeReused()) states.put(stateType.getName(), nextState);
                    isNewState = true;
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException("The state could not be instantiated");
                }
            }

            nextState.onPrepare(this.args, () -> prepared = true);
            Gdx.input.setInputProcessor(null);
            // Notify state (if there is none then we will directly animate the fade in)
            if (currentState != null) currentState.onTransitionOutStart();
            else {
                remainingOutLength = 0;
                switchStates();
            }
        }

        public void render() {
            color.a = hasSwitched ? remainingInLength / inLength : 1 - remainingOutLength / outLength;

            batch.begin();
            batch.setColor(color.clamp());
            batch.draw(Textures.WHITE_PIXEL, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
            batch.end();
        }
        public void update(float delta) {
            if (!prepared && !hasSwitched) nextState.onPrepareUpdate(delta);
            if (hasSwitched && (remainingInLength -= delta) <= 0) completeTransition();
            else if (!hasSwitched && (remainingOutLength -= delta) <= 0) switchStates();
        }
        protected void completeTransition() {
            if (currentState != null) currentState.onTransitionInFinish();
            if (transitionState == this) transitionState = null;
        }
        protected void switchStates() {
            if (!prepared) return;
            if (currentState != null) {
                currentState.onTransitionOutFinish();
                if (!currentState.shouldBeReused()) currentState.onDispose();
            }
            nextState.onTransitionInStart();
            Gdx.input.setInputProcessor(nextState.getInputProcessor());
            currentState = nextState;
            hasSwitched = true;
        }
    }

    public interface Notifier {
        void prepared();
    }
}
