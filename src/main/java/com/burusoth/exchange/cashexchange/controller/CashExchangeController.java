package com.burusoth.exchange.cashexchange.controller;

import com.burusoth.exchange.cashexchange.exception.FileInputFormatException;
import com.burusoth.exchange.cashexchange.exception.InvalidCurrencyException;
import com.burusoth.exchange.cashexchange.response.CashExchangeError;
import com.burusoth.exchange.cashexchange.response.ExchangeRate;
import com.burusoth.exchange.cashexchange.response.ExchangeRateRange;
import com.burusoth.exchange.cashexchange.service.ExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
            return new ResponseEntity<>(new CashExchangeError("Error while getting exchange rates"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileInputFormatException e) {
            logger.error("File format error in the input file");
            e.printStackTrace();
            return new ResponseEntity<>(new CashExchangeError("Error while getting exchange rates"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/exchange/{date}/{cur1}/{cur2}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> exchange(@PathVariable("date") String date, @PathVariable("cur1") String cur1, @PathVariable("cur2") String cur2) throws FileInputFormatException, InvalidCurrencyException, IOException {
        try {
            while(true){
                Future<ExchangeRate> future = exchangeService.getExchangeRate(date, cur1.toUpperCase(), cur2.toUpperCase());
                if(future.isDone()){
                    return new ResponseEntity<>(future.get(), HttpStatus.OK);
                }
                logger.info("Getting delayed to get the response");
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CashExchangeError("Error while getting exchange rates"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileInputFormatException|InterruptedException|ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CashExchangeError("Error while getting exchange rates"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidCurrencyException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CashExchangeError("Invalid Currency Code"), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/exchange")
    @ResponseBody
    public ResponseEntity<?> exchange(@RequestParam String from, @RequestParam String to) {
        try {
            while(true) {
                Future<ExchangeRateRange> future = exchangeService.getExchangeRate(from, to);
                if(future.isDone()){
                    return new ResponseEntity<>(future.get(), HttpStatus.OK);
                }
                logger.info("Getting delayed to get the response");
                Thread.sleep(1000);
            }
        } catch (ParseException | IOException | FileInputFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CashExchangeError("Error while getting exchange rates"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
