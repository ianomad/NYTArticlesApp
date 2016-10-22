/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.models;

public class Meta {
    int hits;
    int time;
    int offset;

    @Override
    public String toString() {
        return "Meta{" +
                "hits=" + hits +
                ", time=" + time +
                ", offset=" + offset +
                '}';
    }
}
