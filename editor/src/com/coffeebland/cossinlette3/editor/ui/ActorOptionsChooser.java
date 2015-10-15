package com.coffeebland.cossinlette3.editor.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.coffeebland.cossinlette3.game.file.PersonDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Guillaume on 2015-09-22.
 */
public class ActorOptionsChooser extends Dialog {

    public static final TextField.TextFieldFilter
            FLOAT_FILTER = (field, c) ->
                    Character.isDigit(c)
                    || (c == '.' && !field.getText().contains(".")),
            ORIENTATION_FILTER = (field, c) ->
                    FLOAT_FILTER.acceptChar(field, c)
                    || (c == '-' && !field.getText().startsWith("-")),
            NAME_FILTER = (field, c) ->
                    "0123456789abcdefghijklmnopqrstuvwxyz".indexOf(Character.toLowerCase(c)) != -1;

    @NtN Listener listener;

    TextButton btnOk, btnCancel;

    Label lblName, lblRadius, lblSpeed, lblDensity, lblOrientation,
            lblCharset;

    PropTextField txtName, txtRadius, txtSpeed, txtDensity, txtOrientation;
    SelectBox<String> sltCharset;

    public ActorOptionsChooser(@NtN String title, @NtN Skin skin, @NtN Listener listener) {
        this(title, skin, null, listener);
    }
    public ActorOptionsChooser(@NtN String title, @NtN Skin skin, @N PersonDef def, @NtN Listener listener) {
        super(title, skin);
        this.listener = listener;

        getContentTable().top().left();

        lblName = new Label("Nom:", skin);
        lblName.setAlignment(Align.left);
        txtName = new PropTextField("", skin);
        txtName.setTextFieldFilter(NAME_FILTER);
        txtName.setMaxLength(20);

        lblRadius = new Label("Radius:", skin);
        lblRadius.setAlignment(Align.left);
        txtRadius = new PropTextField("1", skin);
        txtRadius.setTextFieldFilter(FLOAT_FILTER);
        txtRadius.setMaxLength(5);

        lblSpeed = new Label("Vitesse:", skin);
        lblSpeed.setAlignment(Align.left);
        txtSpeed = new PropTextField("1", skin);
        txtSpeed.setTextFieldFilter(FLOAT_FILTER);
        txtSpeed.setMaxLength(5);

        lblDensity = new Label("Densit\u00E9:", skin);
        lblDensity.setAlignment(Align.left);
        txtDensity = new PropTextField("1", skin);
        txtDensity.setTextFieldFilter(FLOAT_FILTER);
        txtDensity.setMaxLength(5);

        lblOrientation = new Label("Orientation:", skin);
        lblOrientation.setAlignment(Align.left);
        txtOrientation = new PropTextField("0", skin);
        txtOrientation.setTextFieldFilter(ORIENTATION_FILTER);
        txtOrientation.setMaxLength(4);

        lblCharset = new Label("Charset:", skin);
        lblCharset.setAlignment(Align.left);
        sltCharset = new SelectBox<>(skin);
        String[] charsets = Stream.of(new File("img/game").list((f, n) -> n.endsWith(".charset.json")))
                .map(n -> n.replace(".charset.json", ""))
                .toArray(String[]::new);
        String[] tmp = new String[charsets.length + 1];
        //noinspection ConstantConditions
        Arrays.setAll(tmp, i -> i == 0 ? "" : charsets[i - 1]);
        sltCharset.setItems(tmp);

        if (def != null) {
            txtName.setText(def.name);
            txtRadius.setText(String.valueOf(def.radius));
            txtSpeed.setText(String.valueOf(def.speed));
            txtDensity.setText(String.valueOf(def.density));
            txtOrientation.setText(String.valueOf(def.orientation / Math.PI * 8));
            if (def.hasCharset()) {
                sltCharset.setSelected(def.charset);
            }
        }

        btnOk = new TextButton("Ok", skin);
        button(btnOk, true);
        key(Input.Keys.ENTER, true);

        btnCancel = new TextButton("Annuler", skin);
        button(btnCancel, false);
        key(Input.Keys.ESCAPE, false);
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Table content = getContentTable();

        content.pad(8);

        content.row();
        content.add(lblName);
        content.add(txtName).fillX().expandX().colspan(3);

        content.row();
        content.add(lblRadius);
        content.add(txtRadius);
        content.add(lblDensity);
        content.add(txtDensity);

        content.row();
        content.add(lblSpeed);
        content.add(txtSpeed);
        content.add(lblOrientation);
        content.add(txtOrientation);

        content.row();
        content.add(lblCharset);
        content.add(sltCharset).fillX().expandX().colspan(3);

        return super.show(stage, action);
    }

    @Override
    protected void result(Object object) {
        boolean result = (boolean)object;
        if (result) {
            try {
                String name = txtName.getText();
                float radius = Float.parseFloat(txtRadius.getText());
                float density = Float.parseFloat(txtDensity.getText());
                float speed = Float.parseFloat(txtSpeed.getText());
                float orientation = (float)(Float.parseFloat(txtOrientation.getText()) * Math.PI / 8);
                String charset = sltCharset.getSelected();
                if (!(radius > 0 && density > 0)) cancel();
                else listener.result(new Result(name.isEmpty() ? null : name, radius, speed, density, orientation, charset));
            } catch (NumberFormatException ex) {
                cancel();
            }
        }
    }

    public interface Listener {
        void result(@NtN Result result);
    }

    public static class Result {
        @N public String name;
        public float radius, speed, density, orientation;
        @N public String charset;

        public Result(@N String name, float radius, float speed, float density, float orientation, @N String charset) {
            this.name = name;
            this.radius = radius;
            this.speed = speed;
            this.density = density;
            this.orientation = orientation;
            this.charset = charset;
        }

        public void setFor(@NtN PersonDef def) {
            def.name = name;
            def.radius = radius;
            def.speed = speed;
            def.density = density;
            def.charset = charset;
            def.orientation = orientation;
        }
    }
}
