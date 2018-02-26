package com.burusoth.exchange.cashexchange.util;

import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ParserUtilTest {

    @Autowired
    private ParserUtil parserUtil;

    @Test
    public void parseDataShouldReturnMapOfCurrencyCodeAndRespectedValuesWhenRightListIsPassed() throws FileInputFormatException {
        List<String> records = new LinkedList<>();
        records.add("1 SGD traded at 1.04 times USD");
        Map<String,Double> exchangeRates = parserUtil.parseData(records);
        assertTrue(exchangeRates.get("SGD") == 1.04);
    }

    @Test(expected = FileInputFormatException.class)
    public void parseDataShouldReturnFileInputFormatExceptionWhenDataIsNotInFormat() throws FileInputFormatException {
        List<String> records = new LinkedList<>();
        records.add("1 SGD traded 1.04 times USD");
        Map<String,Double> exchangeRates = parserUtil.parseData(records);
    }

    @Test(expected = NumberFormatException.class)
    public void parseDataShouldReturnNumberFormatExceptionWhenDoubleIsNotFormatted() throws FileInputFormatException {
        List<String> records = new LinkedList<>();
        records.add("1 SGD traded to 1.04$ times USD");
        Map<String,Double> exchangeRates = parserUtil.parseData(records);
    }
}