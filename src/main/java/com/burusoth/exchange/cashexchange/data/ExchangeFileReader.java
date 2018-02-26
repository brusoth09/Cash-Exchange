package com.burusoth.exchange.cashexchange.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ExchangeFileReader {
    private String filePathPrefix = "src/main/resources/";
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public List<String> readExchangeRate(String fileName) throws IOException {
        logger.info(filePathPrefix+fileName+".txt");
        return Files.readAllLines(Paths.get(filePathPrefix + fileName + ".txt"));
    }
}
