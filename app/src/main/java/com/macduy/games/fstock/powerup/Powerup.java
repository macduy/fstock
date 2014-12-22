package com.macduy.games.fstock.powerup;

import com.macduy.games.fstock.GameState;
import com.macduy.games.fstock.StockPrice;

/**
 * PowerUps that can affect the current game.
 */
public interface Powerup {
    /** @return Name of the power up. Should be short. */
    String getName();

    /** Applies the power up. */
    void apply(Applicator applicator);

    /** Interface through which powerups are allowed to affect the game. */
    public interface Applicator {
        GameState getGameState();
        StockPrice getStockPrice();
    }
}
