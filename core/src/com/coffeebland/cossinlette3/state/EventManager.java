package com.coffeebland.cossinlette3.state;

import com.coffeebland.cossinlette3.utils.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by Guillaume on 2015-10-08.
 */
public class EventManager {

    protected static class Entry {
        public float remaining;
        @NotNull public Runnable runnable;
        @Nullable public Tag tag;

        public Entry(@Nullable Tag tag, float remaining, @NotNull Runnable runnable) {
            this.remaining = remaining;
            this.runnable = runnable;
            this.tag = tag;
        }

        public boolean shouldRun(@NotNull BitSet tags, float delta) {
            return (tag == null || tags.get(tag.ordinal()))
                    && (remaining -= delta) <= 0;
        }
    }

    protected BitSet tags = new BitSet(Tag.values().length);
    protected List<Entry> queue = new ArrayList<>();

    @NotNull public Entry post(@NotNull Runnable runnable) {
        return post(null, 0, runnable);
    }
    @NotNull public Entry post(float time, @NotNull Runnable runnable) {
        return post(null, time, runnable);
    }
    @NotNull public Entry post(@NotNull Tag tag, @NotNull Runnable runnable) {
        return post(tag, 0, runnable);
    }
    @NotNull public Entry post(@Nullable Tag tag, float time, @NotNull Runnable runnable) {
        Entry entry = new Entry(tag, time, runnable);
        queue.add(entry);
        return entry;
    }

    public void setTagTo(@NotNull Tag tag, boolean value) {
        tags.set(tag.ordinal(), value);
        if (value) update(0);
    }
    public void runTag(@NotNull Tag tag) {
        tags.set(tag.ordinal());
        update(0);
    }
    public void cancelTag(@NotNull Tag tag) {
        tags.clear(tag.ordinal());
    }
    public void cancelForTag(@NotNull Tag tag) {
        queue.removeIf(e -> e.tag == tag);
    }
    public void cancelTagAndEntries(@NotNull Tag tag) {
        cancelTag(tag);
        cancelForTag(tag);
    }

    public void update(float delta) {
        List<Entry> removed = new ArrayList<>();
        queue.removeIf(e -> e.shouldRun(tags, delta) && removed.add(e));
        for (Entry entry : removed) entry.runnable.run();
    }
}
