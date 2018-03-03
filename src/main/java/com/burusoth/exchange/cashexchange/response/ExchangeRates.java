package com.burusoth.exchange.cashexchange.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ExchangeRates {
    private String date;
    @JsonProperty("exchange-rates")
    private Map<String,Double> exchangeRates;

    public ExchangeRates(String date, Map<String, Double> exchangeRates) {
        this.date = date;
        this.exchangeRates = exchangeRates;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(Map<String, Double> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}
