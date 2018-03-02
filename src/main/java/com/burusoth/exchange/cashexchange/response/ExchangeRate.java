package com.burusoth.exchange.cashexchange.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeRate {
    @JsonProperty("currency_1")
    private String currency1;
    @JsonProperty("currency_2")
    private String currency2;
    @JsonProperty("exchange_rate")
    private Double exchangeRate;

    public ExchangeRate(String currency1, String currency2, Double exchangeRate) {
        this.currency1 = currency1;
        this.currency2 = currency2;
        this.exchangeRate = exchangeRate;
    }

    public String getCurrency1() {
        return currency1;
    }

    public void setCurrency1(String currency1) {
        this.currency1 = currency1;
    }

    public String getCurrency2() {
        return currency2;
    }

    public void setCurrency2(String currency2) {
        this.currency2 = currency2;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
