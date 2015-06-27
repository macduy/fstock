package com.macduy.games.fstock.dependency;

import com.macduy.games.fstock.Clock;

import dagger.Module;
import dagger.Provides;

@Module
public class FStockModule {
    @Provides
    Clock provideClock() {
        return new Clock();
    }
}
