package com.macduy.games.fstock.dependency;

import com.macduy.games.fstock.money.Account;
import com.macduy.games.fstock.multitrading.MultiTradingController;

import javax.inject.Singleton;

import dagger.Component;

@Component(
        modules = {
                FStockModule.class
        }
)
@Singleton
public interface FStockComponent {
    MultiTradingController multiTradingController();
    Account account();
}
