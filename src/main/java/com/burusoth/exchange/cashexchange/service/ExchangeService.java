package com.burusoth.exchange.cashexchange.service;

import com.burusoth.exchange.cashexchange.cache.LRUCache;
import com.burusoth.exchange.cashexchange.data.ExchangeFileReader;
import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.exception.InvalidCurrencyException;
import com.burusoth.exchange.cashexchange.response.ExchangeRate;
import com.burusoth.exchange.cashexchange.response.ExchangeRateRange;
import com.burusoth.exchange.cashexchange.response.ExchangeRates;
import com.burusoth.exchange.cashexchange.util.DateUtil;
import com.burusoth.exchange.cashexchange.util.ParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

@Service
public class ExchangeService {

    private ExchangeFileReader exchangeFileReader;
    private ParserUtil parserUtil;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private static LRUCache lruCache;

    static {
        lruCache = new LRUCache(20);
    }

    @Autowired
    public ExchangeService(ExchangeFileReader exchangeFileReader, ParserUtil parserUtil) {
        this.exchangeFileReader = exchangeFileReader;
        this.parserUtil = parserUtil;
    }

    public Map<String, Double> getAllExchangeRate(String date) throws IOException, FileInputFormatException {
        logger.info("Getting records from file..");
        List<String> records = exchangeFileReader.readExchangeRate(date);
        logger.info("Checking cache....");
        Map<String, Double> cachedValue = lruCache.get(date);
        if(cachedValue == null) {
            logger.info("Parsing received records");
            Map<String, Double> exchangeRates = parserUtil.parseData(records);
            lruCache.set(date, exchangeRates);
            return exchangeRates;
        }
        return cachedValue;
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

    public Future<ExchangeRateRange> getExchangeRate(String from, String to) throws ParseException, IOException, FileInputFormatException {
        ExchangeRateRange exchangeRateRange = new ExchangeRateRange(from, to, new ArrayList<>());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date start = df.parse(from);
        Date end = df.parse(to);
        while(start.compareTo(end) <= 0){
            ExchangeRates exchangeRates = new ExchangeRates(df.format(start), new HashMap<>());
            logger.info("Getting records from file..");
            List<String> records = exchangeFileReader.readExchangeRate(df.format(start));
            logger.info("Parsing received records");
            Map<String, Double> ex = parserUtil.parseData(records);
            exchangeRates.setExchangeRates(ex);
            exchangeRateRange.getExchangeRates().add(exchangeRates);
            start = DateUtil.addDays(start, 1);
        }
        return new AsyncResult<>(exchangeRateRange);
    }
}
