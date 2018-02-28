package com.burusoth.exchange.cashexchange.controller;

import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.service.ExchangeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        map.put("sgd",1.2);
        map.put("lkr",100.0);
        Mockito.when(exchangeService.getAllExchangeRate("27-02-2018")).thenReturn(map);
        mockMvc.perform(get("/api/exchange/27-02-2018")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$.sgd").value(1.2))
        .andDo(print());
    }
}