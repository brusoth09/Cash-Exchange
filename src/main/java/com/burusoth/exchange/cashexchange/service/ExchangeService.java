package com.burusoth.exchange.cashexchange.service;

import com.burusoth.exchange.cashexchange.data.ExchangeFileReader;
import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.exception.InvalidCurrencyException;
import com.burusoth.exchange.cashexchange.response.ExchangeRate;
import com.burusoth.exchange.cashexchange.util.ParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class ExchangeService {

    private ExchangeFileReader exchangeFileReader;
    private ParserUtil parserUtil;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    public ExchangeService(ExchangeFileReader exchangeFileReader, ParserUtil parserUtil) {
        this.exchangeFileReader = exchangeFileReader;
        this.parserUtil = parserUtil;
    }

    public Map<String, Double> getAllExchangeRate(String date) throws IOException, FileInputFormatException {
        logger.info("Getting records from file..");
        List<String> records = exchangeFileReader.readExchangeRate(date);
        logger.info("Parsing received records");
        Map<String, Double> exchangeRates = parserUtil.parseData(records);
        return exchangeRates;
    }

    @Async
    public Future<ExchangeRate> getExchangeRate(String date, String cur1, String cur2) throws IOException, FileInputFormatException, InvalidCurrencyException {
        logger.info("Getting records from file..");
        List<String> records = exchangeFileReader.readExchangeRate(date);
        logger.info("Parsing received records");
        Map<String, Double> exchangeRates = parserUtil.parseData(records);

        if(exchangeRates.containsKey(cur1.toUpperCase()) && exchangeRates.containsKey(cur2.toUpperCase())){
            return new AsyncResult<>( new ExchangeRate(cur1.toUpperCase(), cur2.toUpperCase(), exchangeRates.get(cur1.toUpperCase())/exchangeRates.get(cur2.toUpperCase())));
        }else{
            throw new InvalidCurrencyException("Currency Code is Invalid");
        }
    }
}
