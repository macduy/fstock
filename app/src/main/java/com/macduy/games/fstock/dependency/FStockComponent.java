package com.macduy.games.fstock.dependency;

import com.macduy.games.fstock.multitrading.MultiTradingController;

import dagger.Component;

@Component(
        modules = {
                FStockModule.class
        }
)
public interface FStockComponent {
    MultiTradingController multiTradingController();
}
