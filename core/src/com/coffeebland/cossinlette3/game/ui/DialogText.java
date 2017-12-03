package com.coffeebland.cossinlette3.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.V2;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class DialogText extends DialogInteraction {

    public static final float THROTTLE_FACTOR = 3f;

    @NtN protected final BitmapFont font;
    @NtN protected final String rawText;
    @NtN protected final TimeUntilLetterSource timeUntilLetterSource;

    protected final int lineCount;
    protected int currentLine, displayedLetters, displayedLines;
    protected float timeUntilLetter;
    @NtN protected String computedText = "";

    @NtN protected GlyphLayout glyphLayout;
    protected int layoutLetterCount;
    @N protected Array<GlyphLayout.GlyphRun> futureRuns;
    @N protected GlyphLayout.GlyphRun currentRun;
    @N protected Array<BitmapFont.Glyph> currentRunGlyphs;
    protected boolean glyphIsDirty = true, throttling = false, followText = true;

    public DialogText(
            @NtN String displayName,
            @NtN BitmapFont font,
            int width, int lineCount,
            @NtN Drawable window,
            @NtN String rawText,
            @NtN TimeUntilLetterSource timeUntilLetterSource,
            @N Listener listener
    ) {
        super(displayName, V2.get(width, font.getLineHeight() * lineCount), window, listener);

        this.font = font;
        this.lineCount = lineCount;
        this.rawText = rawText;
        this.timeUntilLetterSource = timeUntilLetterSource;

        glyphLayout = new GlyphLayout();
        font.getData().markupEnabled = true;
    }

    @N protected Character getPreviousChar() {
        return displayedLetters > 0 ?
                computedText.charAt(displayedLetters - 1) : null;
    }
    @N protected Character getNextChar() {
        return displayedLetters < computedText.length() ?
                computedText.charAt(displayedLetters) : null;
    }
    @Override public void update(float delta) {
        super.update(delta);

        letters: {
            if (displayedLetters == computedText.length()) break letters;

            timeUntilLetter -= delta * (throttling ? THROTTLE_FACTOR : 1);
            while (timeUntilLetter <= 0 && displayedLetters < computedText.length()) {
                timeUntilLetter += timeUntilLetterSource.eval(++displayedLetters, getNextChar(), getPreviousChar());
                glyphIsDirty = true;
            }
        }
    }
    @Override public boolean updateInput(float delta) {
        throttling =
                Gdx.input.isKeyPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyPressed(Input.Keys.C);
        return super.updateInput(delta);
    }
    @Override public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ENTER:
            case Input.Keys.SPACE:
            case Input.Keys.C:
                if (displayedLetters >= computedText.length()) close();
                return true;
            case Input.Keys.UP:
                currentLine = Math.max(currentLine - 1, 0);
                followText = currentLine == Math.max(displayedLines - lineCount, 0);
                glyphIsDirty = true;
                return true;
            case Input.Keys.DOWN:
                currentLine = Math.min(currentLine + 1, Math.max(displayedLines - lineCount, 0));
                followText = currentLine == Math.max(displayedLines - lineCount, 0);
                glyphIsDirty = true;
                return true;
            default:
                return super.keyDown(keycode);
        }
    }
    @Override public void renderContent(@NtN Batch batch, @NtN Vector2 pos, @NtN Vector2 size) {
        batch.flush();
        ScissorStack.pushScissors(new Rectangle(pos.x, pos.y, size.x, size.y));

        checkGlyphLayout(size);
        float yOffset = (font.getLineHeight() - font.getAscent() - font.getCapHeight() - font.getDescent()) / 2;
        font.draw(batch, glyphLayout, pos.x, pos.y + size.y - yOffset + currentLine * font.getLineHeight());

        batch.flush();
        ScissorStack.popScissors();
    }

    protected void computeInitialGlyphLayout(@NtN Vector2 size) {
        glyphLayout.setText(font, rawText, Color.BLACK, size.x, Align.left, true);
        StringBuilder text = new StringBuilder();
        GlyphLayout.GlyphRun lastRun = null;
        for (GlyphLayout.GlyphRun run: glyphLayout.runs) {
            if (lastRun != null && lastRun.y != run.y) text.append("\n");
            for (BitmapFont.Glyph glyph: run.glyphs) text.append(glyph.toString());
            lastRun = run;
        }

        computedText = text.toString();
        futureRuns = new Array<>(glyphLayout.runs);
        futureRuns.reverse();
        glyphLayout.runs.clear();
    }
    protected void addMissingGlyphs(@NtN Array<GlyphLayout.GlyphRun> futureRuns) {
        while (layoutLetterCount < displayedLetters) {
            if (currentRunGlyphs == null || currentRunGlyphs.size == 0) {
                @N GlyphLayout.GlyphRun lastRun = currentRun;
                currentRun = futureRuns.pop();
                currentRunGlyphs = new Array<>(currentRun.glyphs);
                currentRunGlyphs.reverse();
                currentRun.glyphs.clear();
                glyphLayout.runs.add(currentRun);

                if (lastRun != null && lastRun.y != currentRun.y) {
                    layoutLetterCount++;
                    continue;
                }
            }
            assert currentRun != null;

            currentRun.glyphs.add(currentRunGlyphs.pop());
            layoutLetterCount++;
        }
    }
    protected void computeGlyphLines() {
        float minY = 0, maxY = 0;
        for (GlyphLayout.GlyphRun run: glyphLayout.runs) {
            minY = Math.min(run.y, minY);
            maxY = Math.max(run.y, maxY);
        }
        displayedLines = (int)((maxY - minY) / font.getLineHeight() + 1);
    }
    protected void checkGlyphLayout(@NtN Vector2 size) {
        if (glyphIsDirty) {
            if (futureRuns == null) computeInitialGlyphLayout(size);
            assert futureRuns != null;
            addMissingGlyphs(futureRuns);
            computeGlyphLines();
            if (followText) currentLine = Math.max(displayedLines - lineCount, 0);
            glyphIsDirty = false;
        }
    }

    public interface TimeUntilLetterSource {
        float eval(int displayedLetters, @N Character nextChar, @N Character previousChar);
    }
}