package com.macduy.games.fstock.powerup;

import com.macduy.games.fstock.GameState;

/**
 * Injects an absolute amount of money into the player's current balance.
 */
public class CashInjectionPowerup implements Powerup {
    private final float mValue;
    private final String mName;

    public CashInjectionPowerup(float value) {
        mValue = value;
        mName = String.format("+£%.2f", mValue);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void apply(Applicator applicator) {
        GameState gameState = applicator.getGameState();
        gameState.setCurrentMoney(gameState.getCurrentMoney() + mValue);
    }
}
