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
import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;
import com.coffeebland.cossinlette3.utils.V2;

/**
 * Created by Guillaume on 2015-10-15.
 */
public class DialogText extends DialogInteraction {

    public static final float THROTTLE_FACTOR = 3f;

    @NtN protected final BitmapFont font;
    @NtN protected final String text;
    @NtN protected final TimeUntilLetterSource timeUntilLetterSource;

    protected final int lineCount;
    protected int currentLine, displayedLetters, displayedLines;
    protected float timeUntilLetter;

    @NtN protected GlyphLayout glyphLayout;
    protected boolean glyphIsDirty = true, throttling = false, followText = true;

    public DialogText(
            @NtN String displayName,
            @NtN BitmapFont font,
            int width, int lineCount,
            @NtN Drawable window,
            @N Listener listener,
            @NtN String text,
            @NtN TimeUntilLetterSource timeUntilLetterSource
    ) {
        super(displayName, V2.get(width, font.getLineHeight() * lineCount), window, listener);
        this.font = font;
        this.lineCount = lineCount;
        this.text = text;
        this.timeUntilLetterSource = timeUntilLetterSource;
        glyphLayout = new GlyphLayout();
        font.getData().markupEnabled = true;
    }

    @Override public void update(float delta) {
        super.update(delta);

        letters: {
            if (displayedLetters == text.length()) break letters;

            timeUntilLetter -= delta * (throttling ? THROTTLE_FACTOR : 1);
            while (timeUntilLetter <= 0 && displayedLetters < text.length()) {
                timeUntilLetter += timeUntilLetterSource.eval(
                        ++displayedLetters,
                        displayedLetters < text.length() ? text.charAt(displayedLetters) : null,
                        displayedLetters > 0 ? text.charAt(displayedLetters - 1) : null
                );
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
                if (displayedLetters >=  text.length()) close();
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

    protected static int seekNextWhitespace(String text, int start) {
        while (start < text.length() && !Character.isWhitespace(text.charAt(start))) start++;
        return start;
    }
    protected static void removeTextFromGlyphs(String text, GlyphLayout layout, int startInclusive, int endExclusive) {
        GlyphIndex glyphIndex = new GlyphIndex();
        BitmapFont.Glyph glyph = null;
        for (int i = endExclusive - 1; i >= startInclusive; i--) {
            char src = text.charAt(i);
            if (Character.isWhitespace(src)) continue;
            /*if (src == ']') {
                // Either this was the end of a color or pop tag, or it's just a lonely bracket;
                // if we can find a corresponding tag, we'll assume it wasn't just lonely

                // First, let's check if this is a pop tag; if it is, let's back track accordingly
                if (text.charAt(i - 1) == '[' && text.charAt(i-))
                for (int si = i - 1; si >= 0; si--) {
                    char candidate = text.charAt(si);
                    if (candidate == '[') {

                    }
                }
            }*/
            glyph = glyph == null ? glyphIndex.getLastGlyph(layout) : glyphIndex.getPreviousGlyph(layout);
        }
        if (glyph == null) return;

        // The edge case where the runs or glyphs are empty should be covered by the null check just above
        if (glyphIndex.runIndex + 1 < layout.runs.size) layout.runs.removeRange(glyphIndex.runIndex + 1, layout.runs.size - 1);
        GlyphLayout.GlyphRun run = layout.runs.get(glyphIndex.runIndex);
        run.glyphs.removeRange(glyphIndex.glyphIndex, run.glyphs.size - 1);
    }
    protected void computeGlyphLines() {
        float lastY = 0;
        GlyphLine currentLine = null;
        for (GlyphLayout.GlyphRun run : glyphLayout.runs) {
            if (currentLine == null || run.y != lastY) {
                currentLine = new GlyphLine(currentLine != null ? currentLine.start + currentLine.count + 1 : 0, 0);
                lastY = run.y;
            }
            currentLine.count += run.glyphs.size;
        }
        displayedLines = -(int)(glyphLayout.runs.get(glyphLayout.runs.size - 1).y / font.getLineHeight()) + 1;
    }

    protected void checkGlyphLayout(@NtN Vector2 size) {
        if (glyphIsDirty) {
            int nextWhitespace = seekNextWhitespace(text, displayedLetters);
            glyphLayout.setText(font, text.substring(0, nextWhitespace), Color.BLACK, size.x, Align.left, true);
            computeGlyphLines();
            if (followText) currentLine = Math.max(displayedLines - lineCount, 0);
            removeTextFromGlyphs(text, glyphLayout, displayedLetters, nextWhitespace);
            glyphIsDirty = false;
        }
    }

    public interface TimeUntilLetterSource {
        float eval(int displayedLetters, @N Character nextChar, @N Character previousChar);
    }

    public static class GlyphLine {
        public int start, count;

        public GlyphLine(int start, int count) {
            this.start = start;
            this.count = count;
        }

        public int getEnd() { return start + count; }
    }
    public static class GlyphIndex {
        int runIndex, glyphIndex;

        public GlyphIndex() {}

        @N public BitmapFont.Glyph getLastGlyph(GlyphLayout layout) {
            if (layout.runs.size == 0) return null;
            runIndex = layout.runs.size - 1;
            GlyphLayout.GlyphRun run = layout.runs.get(runIndex);
            if (run.glyphs.size == 0) return null;
            glyphIndex = run.glyphs.size - 1;
            return run.glyphs.get(glyphIndex);
        }
        @N public BitmapFont.Glyph getPreviousGlyph(GlyphLayout layout) {
            GlyphLayout.GlyphRun run;
            if (glyphIndex == 0) {
                if (runIndex == 0) return null;
                runIndex--;
                run = layout.runs.get(runIndex);
                if (run.glyphs.size == 0) return null;
                glyphIndex = run.glyphs.size - 1;
            } else {
                run = layout.runs.get(runIndex);
                glyphIndex--;
            }
            return run.glyphs.get(glyphIndex);
        }
    }
}