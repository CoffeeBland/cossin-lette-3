package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.Dst;
import com.coffeebland.cossinlette3.utils.V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameCamera {
    @NotNull protected OrthographicCamera camera;
    @NotNull protected Vector2 pos;
    @Nullable protected PositionSource target;
    @NotNull protected GameWorld gameWorld;
    protected float moveRatio = 0.05f;

    public GameCamera(@NotNull GameWorld gameWorld) {
        camera = new OrthographicCamera();
        pos = V2.get();
        this.gameWorld = gameWorld;
        updateToSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public OrthographicCamera underlyingCamera() {
        return camera;
    }
    @NotNull public Vector2 getPos() { return pos; }

    public void updateToSize(int width, int height) {
        camera.setToOrtho(
                false,
                width * Const.METERS_PER_PIXEL,
                height * Const.METERS_PER_PIXEL
        );
        setPosAndUpdateCamera(pos);
    }

    public void moveTo(@NotNull PositionSource source) {
        target = source;
    }
    public void moveTo(@NotNull Vector2 pos) {
        target = () -> pos;
    }
    public void setTo(@NotNull PositionSource source) {
        target = source;
        if (source.getPosition() != null) {
            setPosAndUpdateCamera(source.getPosition());
        }
    }
    public void setTo(@NotNull Vector2 pos) {
        setTo(() -> pos);
    }
    protected void setPosAndUpdateCamera(@NotNull Vector2 pos) {
        float hW = Dst.getAsMeters(Gdx.graphics.getWidth() / 2);
        float hH  = Dst.getAsMeters(Gdx.graphics.getHeight() / 2);
        camera.position.set(
                V2.clamp(
                        this.pos.set(pos),
                        hW, (float)gameWorld.getWidth() - hW,
                        hH, (float)gameWorld.getHeight() - hH
                ),
                camera.position.z
        );
        camera.update();
    }

    public void setMoveRatio(float ratio) {
        moveRatio = ratio;
    }

    public void update(float delta) {
        if (target != null) {
            Vector2 targetV2 = target.getPosition();
            if (targetV2 != null) {
                Vector2 scaledPos = V2.get(pos).scl(1 - moveRatio);
                Vector2 scaledTarget = V2.get(targetV2).scl(moveRatio);
                setPosAndUpdateCamera(pos.set(scaledPos).add(scaledTarget));
                V2.claim(scaledPos);
                V2.claim(scaledTarget);
            }
        }
    }

    public interface PositionSource {
        @Nullable Vector2 getPosition();
    }
}