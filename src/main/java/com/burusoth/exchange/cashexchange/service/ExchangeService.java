package com.burusoth.exchange.cashexchange.service;

import com.burusoth.exchange.cashexchange.data.ExchangeFileReader;
import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.util.ParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
}
