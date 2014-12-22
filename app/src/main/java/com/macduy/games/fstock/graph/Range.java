package com.macduy.games.fstock.graph;

/**
 * Represents a range with a start and an end. It may not generally hold that end > start.
 */
public interface Range {
    float start();
    float end();
}
