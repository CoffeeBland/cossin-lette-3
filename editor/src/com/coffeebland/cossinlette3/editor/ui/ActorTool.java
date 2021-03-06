package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.coffeebland.cossinlette3.editor.OperationExecutor;
import com.coffeebland.cossinlette3.editor.tools.AbsTool;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import com.coffeebland.cossinlette3.game.file.WorldDef;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.V2;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Guillaume on 2015-09-22.
 */
public class ActorTool extends AbsTool<Operation> {

    protected float iterationsSinceClick;
    @N protected Vector2 lastPos;
    @NtN protected Set<PersonDef> clicked = new HashSet<>();
    @NtN protected Set<PersonDef> invalidated = new HashSet<>();
    @NtN protected Stage stage;
    @NtN protected Skin skin;

    public ActorTool(@NtN Stage stage, @NtN Skin skin) {
        this.stage = stage;
        this.skin = skin;
    }

    @Override
    public void draw(@NtN WorldWidget widget, @NtN Batch batch) {
        invalidated.stream().forEach(widget.charsets::remove);
        invalidated.clear();
    }

    @Override
    public void begin(@NtN WorldDef worldDef) {
        assert initialPosMeters == null;
        initialPosMeters = V2.get(posMeters);

        if (iterationsSinceClick <= 10) {
            Optional<PersonDef> option = clicked.stream()
                    .filter(d -> posMeters.dst2(d.x, d.y) < d.radius * d.radius)
                    .findFirst();
            if (option.isPresent()) {
                PersonDef d = option.get();
                new ActorOptionsChooser("Options de l'acteur", skin, d, r -> {
                    r.setFor(d);
                    invalidated.add(d);
                }).show(stage);
            } else {
                new ActorOptionsChooser("Ajouter un acteur", skin, r -> {
                    PersonDef d = new PersonDef();
                    r.setFor(d);
                    d.x = posMeters.x;
                    d.y = posMeters.y;
                    worldDef.people.add(d);
                }).show(stage);
            }
        }

        clicked.clear();
        worldDef.people.stream()
                .filter(d -> posMeters.dst2(d.x, d.y) < d.radius * d.radius)
                .limit(1)
                .forEach(clicked::add);

        if (lastPos != null) V2.claim(lastPos);
        lastPos = V2.get(posMeters);
        iterationsSinceClick = 0;
    }

    @Override
    public void update(@NtN WorldDef worldDef) {
        iterationsSinceClick++;
        if (initialPosMeters != null) {
            Vector2 diff = V2.get(posMeters).sub(initialPosMeters);
            clicked.stream().forEach(d -> { d.x += diff.x; d.y += diff.y; });
            V2.claim(diff);
            initialPosMeters.set(posMeters);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.FORWARD_DEL)) {
            worldDef.people.removeAll(clicked);
            clicked.clear();
        }
    }

    @Override
    public void complete(@NtN OperationExecutor executor) {
        if (initialPosMeters != null) {
            V2.claim(initialPosMeters);
            initialPosMeters = null;
        }
    }

    @Override
    public void cancel() {
        if (initialPosMeters != null) {
            V2.claim(initialPosMeters);
            initialPosMeters = null;
        }
    }
}
