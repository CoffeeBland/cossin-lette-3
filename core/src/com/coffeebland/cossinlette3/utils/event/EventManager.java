package com.coffeebland.cossinlette3.utils.event;

import com.coffeebland.cossinlette3.utils.N;
import com.coffeebland.cossinlette3.utils.NtN;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by Guillaume on 2015-10-08.
 */
public class EventManager {

    public static class Entry {
        public float remaining;
        @NtN public Runnable runnable;
        @N public Tag tag;

        public Entry(@N Tag tag, float remaining, @NtN Runnable runnable) {
            this.remaining = remaining;
            this.runnable = runnable;
            this.tag = tag;
        }

        public boolean shouldRun(@NtN BitSet tags, float delta) {
            return (tag == null || tags.get(tag.ordinal()))
                    && (remaining -= delta) <= 0;
        }
    }

    protected BitSet tags = new BitSet(Tag.values().length);
    protected List<Entry> queue = new ArrayList<>();

    @NtN public Entry post(@NtN Runnable runnable) {
        return post(null, 0, runnable);
    }
    @NtN public Entry post(float time, @NtN Runnable runnable) {
        return post(null, time, runnable);
    }
    @NtN public Entry post(@NtN Tag tag, @NtN Runnable runnable) {
        return post(tag, 0, runnable);
    }
    @NtN public Entry post(@N Tag tag, float time, @NtN Runnable runnable) {
        Entry entry = new Entry(tag, time, runnable);
        queue.add(entry);
        return entry;
    }

    public void setTagTo(@NtN Tag tag, boolean value) {
        tags.set(tag.ordinal(), value);
        if (value) update(0);
    }
    public void runTag(@NtN Tag tag) {
        tags.set(tag.ordinal());
        update(0);
    }
    public void cancelTag(@NtN Tag tag) {
        tags.clear(tag.ordinal());
    }
    public void cancelForTag(@NtN Tag tag) {
        queue.removeIf(e -> e.tag == tag);
    }
    public void cancelTagAndEntries(@NtN Tag tag) {
        cancelTag(tag);
        cancelForTag(tag);
    }

    public void update(float delta) {
        List<Entry> removed = new ArrayList<>();
        queue.removeIf(e -> e.shouldRun(tags, delta) && removed.add(e));
        for (Entry entry : removed) entry.runnable.run();
    }
}
