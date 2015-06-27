package com.macduy.games.fstock.money;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents a Player's account.
 */
@Singleton
public class Account {
    /** Amount of cash held */
    private double mCash;

    @Inject
    public Account() {
    }

    public void deposit(double cash) {
        mCash += cash;
    }

    public boolean withdraw(double amount) {
        boolean success = false;
        if (amount <= mCash) {
            mCash -= amount;
            success = true;
        }
        return success;
    }

    public double getAmount() {
        return mCash;
    }
}
