package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.utils.Const;
import com.coffeebland.cossinlette3.utils.VPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameCamera {
    @NotNull protected OrthographicCamera camera;
    @NotNull protected Vector2 pos;
    @Nullable protected PositionSource target;
    protected float moveRatio = 0.05f;

    public GameCamera() {
        camera = new OrthographicCamera();
        pos = VPool.V2();
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
        camera.position.set(pos.x, pos.y, camera.position.z);
        camera.update();
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
            camera.position.set((pos.set(source.getPosition())), camera.position.z);
            camera.update();
        }
    }
    public void setTo(@NotNull Vector2 pos) {
        setTo(() -> pos);
    }

    public void setMoveRatio(float ratio) {
        moveRatio = ratio;
    }

    public void update(float delta) {
        if (target != null) {
            Vector2 targetV2 = target.getPosition();
            if (targetV2 != null) {
                Vector2 scaledPos = VPool.V2(pos).scl(1 - moveRatio);
                Vector2 scaledTarget = VPool.V2(targetV2).scl(moveRatio);
                pos.set(scaledPos).add(scaledTarget);
                camera.position.set(pos, camera.position.z);
                camera.update();
                VPool.claim(scaledPos);
                VPool.claim(scaledTarget);
            }
        }
    }

    public interface PositionSource {
        @Nullable Vector2 getPosition();
    }
}
