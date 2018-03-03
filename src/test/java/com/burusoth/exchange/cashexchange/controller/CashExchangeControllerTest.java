package com.burusoth.exchange.cashexchange.controller;

import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.exception.InvalidCurrencyException;
import com.burusoth.exchange.cashexchange.response.ExchangeRate;
import com.burusoth.exchange.cashexchange.response.ExchangeRateRange;
import com.burusoth.exchange.cashexchange.response.ExchangeRates;
import com.burusoth.exchange.cashexchange.service.ExchangeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CashExchangeController.class)
public class CashExchangeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;


    @Test
    public void exchangeEndPointShouldReturnExchangeRatesForSpecifiedDate() throws Exception, FileInputFormatException {
        Map<String, Double> map = new HashMap<>();
        map.put("SGD",1.2);
        map.put("USD",100.0);
        Mockito.when(exchangeService.getAllExchangeRate("27-02-2018")).thenReturn(map);
        mockMvc.perform(get("/api/exchange/27-02-2018")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$.SGD").value(1.2))
        .andDo(print());
    }

    @Test
    public void exchangeEndpointShouldReturnErrorMessageForIPMessages() throws Exception, FileInputFormatException {
        Map<String, Double> map = new HashMap<>();
        map.put("SGD",1.2);
        map.put("USD",100.0);
        Mockito.when(exchangeService.getAllExchangeRate("27-02-2018")).thenThrow(new IOException());
        mockMvc.perform(get("/api/exchange/27-02-2018")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Error while getting exchange rates"))
                .andDo(print());
    }

    @Test
    public void exchangeEndpointShouldReturnErrorMessageWhenFileNotFound() throws Exception, FileInputFormatException {
        Map<String, Double> map = new HashMap<>();
        map.put("SGD",1.2);
        map.put("USD",100.0);
        Mockito.when(exchangeService.getAllExchangeRate("27-02-2018")).thenThrow(new FileInputFormatException("wrong file format"));
        mockMvc.perform(get("/api/exchange/27-02-2018")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Error while getting exchange rates"))
                .andDo(print());
    }

    @Test
    public void exchangeShouldReturnExchangeRateWhenCorrectCodePassed() throws FileInputFormatException, InvalidCurrencyException, Exception {
        Mockito.when(exchangeService.getExchangeRate("27-02-2018", "SGD", "LKR")).thenReturn(new AsyncResult<>(new ExchangeRate("SGD", "LKR", 1.2 / 100.0)));
        mockMvc.perform(get("/api/exchange/27-02-2018/sgd/lkr")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.currency_1").value("SGD"))
                .andExpect(jsonPath("$.currency_2").value("LKR"))
                .andExpect(jsonPath("$.exchange_rate").value(1.2/100.0))
                .andDo(print());
    }

    @Test
    public void exchangeShouldReturnErrorMessageWhenFileNotFound() throws FileInputFormatException, InvalidCurrencyException, Exception {
        Mockito.when(exchangeService.getExchangeRate("27-02-2018","SGD", "LKR")).thenThrow(new InvalidCurrencyException("Invalid currency"));
        mockMvc.perform(get("/api/exchange/27-02-2018/sgd/lkr")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Invalid Currency Code"))
                .andDo(print());
    }

    @Test
    public void exchangeShouldReturnInvalidCurrencyCodeWhenDifferentCurrencyCodeProvided() throws FileInputFormatException, InvalidCurrencyException, Exception {
        Mockito.when(exchangeService.getExchangeRate("27-02-2018", "SGD", "LKR")).thenThrow(new FileInputFormatException("File not found for the date"));
        mockMvc.perform(get("/api/exchange/27-02-2018/sgd/lkr")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Error while getting exchange rates"))
                .andDo(print());
    }

    @Test
    public void exchangeEndpointForExchangeRateShouldThrowErrorMessageForIPMessages() throws Exception, FileInputFormatException, InvalidCurrencyException {
        Mockito.when(exchangeService.getExchangeRate("27-02-2018","SGD","LKR")).thenThrow(new IOException());
        mockMvc.perform(get("/api/exchange/27-02-2018/sgd/lkr")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Error while getting exchange rates"))
                .andDo(print());
    }


    @Test
    public void exchangeShouldReturnExchangeRateForGivenDateRangeWithCorrectValues() throws FileInputFormatException, InvalidCurrencyException, Exception {
        Map<String, Double> rates = new HashMap<>();
        rates.put("SGD", 1.2);
        rates.put("LKR", 1.4);
        ExchangeRates exchangeRates1 = new ExchangeRates("27-02-2018", rates);
        ExchangeRates exchangeRates2 = new ExchangeRates("28-02-2018", rates);
        List<ExchangeRates> exchangeRatesList = new ArrayList<>();
        exchangeRatesList.add(exchangeRates1);
        exchangeRatesList.add(exchangeRates2);
        ExchangeRateRange exchangeRateRange = new ExchangeRateRange("27-02-2018", "28-02-2018", exchangeRatesList);
        Mockito.when(exchangeService.getExchangeRate("27-02-2018", "28-02-2018")).thenReturn(new AsyncResult<>(exchangeRateRange));
        mockMvc.perform(get("/api/exchange/")
                .param("from", "27-02-2018")
                .param("to", "28-02-2018")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.from").value("27-02-2018"))
                .andExpect(jsonPath("$.to").value("28-02-2018"))
                .andExpect(jsonPath("$.rates[0].date").value("27-02-2018"))
                .andExpect(jsonPath("$.rates[0].exchange-rates.SGD").value(1.2))
                .andDo(print());
    }

    @Test
    public void exchangeShouldReturnErrorWhileGettingResponse() throws FileInputFormatException, Exception {
        Mockito.when(exchangeService.getExchangeRate("27-02-2018", "28-02-2018")).thenThrow(FileInputFormatException.class);
        mockMvc.perform(get("/api/exchange/")
                .param("from", "27-02-2018")
                .param("to", "28-02-2018")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(status().isInternalServerError());
    }
}