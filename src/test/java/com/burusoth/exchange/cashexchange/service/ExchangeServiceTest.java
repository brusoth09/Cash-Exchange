package com.burusoth.exchange.cashexchange.service;

import com.burusoth.exchange.cashexchange.data.ExchangeFileReader;
import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.util.ParserUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExchangeServiceTest {
    @InjectMocks
    private ExchangeService exchangeService;
    @Mock
    private ExchangeFileReader exchangeFileReader;
    @Mock
    private ParserUtil parserUtil;

    @Test
    public void getAllExchangeRateShouldReturnAMapContainsExchangeRates() throws IOException, FileInputFormatException {
        String date = "2010-02-24";
        // mock exchange file reader of exchange service
        List<String> list = new LinkedList<>();
        HashMap<String,Double> exchangeRates = new HashMap<>();
        exchangeRates.put("SGD",1.04);
        list.add("1 SGD traded at 1.04 times USD");
        Mockito.when(exchangeFileReader.readExchangeRate(date)).thenReturn(list);
        Mockito.when(parserUtil.parseData(list)).thenReturn(exchangeRates);

        // assert returned values
        Map<String, Double> rates = exchangeService.getAllExchangeRate(date);
        assertTrue(rates.containsKey("SGD"));
    }
}