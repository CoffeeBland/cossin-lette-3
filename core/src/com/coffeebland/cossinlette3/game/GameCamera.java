package com.coffeebland.cossinlette3.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.coffeebland.cossinlette3.utils.*;

public class GameCamera {
    @NtN protected OrthographicCamera camera;
    @NtN protected Vector2 pos;
    @N protected PositionSource target;
    @NtN protected GameWorld gameWorld;
    protected float moveRatio = 0.05f;

    public GameCamera(@NtN GameWorld gameWorld) {
        camera = new OrthographicCamera();
        pos = V2.get();
        this.gameWorld = gameWorld;
        updateToSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public OrthographicCamera underlyingCamera() {
        return camera;
    }
    @NtN public Vector2 getPos() { return pos; }

    public void updateToSize(int width, int height) {
        camera.setToOrtho(
                false,
                width * Const.METERS_PER_PIXEL,
                height * Const.METERS_PER_PIXEL
        );
        setPosAndUpdateCamera(pos);
    }

    public void moveTo(@NtN PositionSource source) {
        target = source;
    }
    public void moveTo(@NtN Vector2 pos) {
        target = () -> pos;
    }
    public void setTo(@NtN PositionSource source) {
        target = source;
        if (source.getPosition() != null) {
            setPosAndUpdateCamera(source.getPosition());
        }
    }
    public void setTo(@NtN Vector2 pos) {
        setTo(() -> pos);
    }
    protected void setPosAndUpdateCamera(@NtN Vector2 pos) {
        float hW = Dst.getAsMeters(Gdx.graphics.getWidth() / 2);
        float hH  = Dst.getAsMeters(Gdx.graphics.getHeight() / 2);
        camera.position.set(
                V2.clamp(
                        this.pos.set(pos),
                        hW, (float) gameWorld.getWidth() - hW,
                        hH, (float) gameWorld.getHeight() - hH
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
        @N Vector2 getPosition();
    }
}