package com.burusoth.exchange.cashexchange.util;

import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ParserUtil {

    private static Set<Currency> currencySet;
    private static  Set<String> currencyCodes = new HashSet<>();

    static {
        currencySet = Currency.getAvailableCurrencies();
        for(Currency currency:currencySet){
            currencyCodes.add(currency.getCurrencyCode());
        }
    }

    public Map<String, Double> parseData(List<String> records) throws FileInputFormatException {
        Map<String,Double> exchangeRates = new HashMap<>();
        for(String s: records){
            String[] tokens = s.split("\\s+");
            if(tokens != null && tokens.length == 7 && currencyCodes.contains(tokens[1])){
                exchangeRates.put(tokens[1], Double.parseDouble(tokens[4]));
            } else {
                throw new FileInputFormatException("Wrong input format...");
            }

        }
        return exchangeRates;
    }
}
