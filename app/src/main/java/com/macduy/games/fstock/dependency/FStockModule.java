package com.macduy.games.fstock.dependency;

import com.macduy.games.fstock.Clock;
import com.macduy.games.fstock.MultiTradingActivity;
import com.macduy.games.fstock.multitrading.MultiTradingController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FStockModule {
    @Provides
    Clock provideClock() {
        return new Clock();
    }
}
