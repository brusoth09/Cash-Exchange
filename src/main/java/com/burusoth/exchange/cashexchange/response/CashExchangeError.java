package com.burusoth.exchange.cashexchange.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class CashExchangeError {
    @JsonProperty("error")
    private String error;

    public CashExchangeError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
