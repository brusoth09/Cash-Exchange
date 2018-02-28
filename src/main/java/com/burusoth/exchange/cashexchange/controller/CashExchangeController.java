package com.burusoth.exchange.cashexchange.controller;

import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.service.ExchangeService;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CashExchangeController {

    @Autowired
    private ExchangeService exchangeService;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Cacheable({"exchanges"})
    @RequestMapping(value = "/exchange/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> exchange(@PathVariable("date")String date){
        try {
            logger.info("Calling service class for exchange rates");
            return new ResponseEntity<>(exchangeService.getAllExchangeRate(date), HttpStatus.OK);
        } catch (IOException e) {
            logger.error("IO error while processing request", e);
            e.printStackTrace();
            return new ResponseEntity<>("Error while getting exchange rates", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileInputFormatException e) {
            logger.error("File format error in the input file");
            e.printStackTrace();
            return new ResponseEntity<>("Error while getting exchange rates", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
