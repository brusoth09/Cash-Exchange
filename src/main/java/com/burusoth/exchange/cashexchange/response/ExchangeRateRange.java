package com.burusoth.exchange.cashexchange.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExchangeRateRange {
    private String from;
    private String to;
    @JsonProperty("rates")
    public List<ExchangeRates> exchangeRates;

    public ExchangeRateRange(String from, String to, List<ExchangeRates> exchangeRates) {
        this.from = from;
        this.to = to;
        this.exchangeRates = exchangeRates;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<ExchangeRates> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRates> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}
