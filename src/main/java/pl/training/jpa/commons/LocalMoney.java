package pl.training.jpa.commons;

import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;

public class LocalMoney {

    private static final Locale DEFAULT_LOCALE = new Locale("pl", "PL");

    public static FastMoney of(Number value) {
        return FastMoney.of(value, currencyUnit());
    }

    public static FastMoney zero() {
        return FastMoney.zero(currencyUnit());
    }

    private static CurrencyUnit currencyUnit() {
        return Monetary.getCurrency(DEFAULT_LOCALE);
    }

}
