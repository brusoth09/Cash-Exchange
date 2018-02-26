package com.burusoth.exchange.cashexchange.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExchangeFileReaderTest {
    @Autowired
    private ExchangeFileReader exchangeFileReader;
    private File file;
    private String fileName = "2014-02-24";
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Before
    public void setUp() throws IOException {
        logger.info("Creating file for testing");
        file = new File("src/main/resources/" + fileName + ".txt");
        boolean isCreated = file.createNewFile();
        if(isCreated){
            logger.info("File Created Successfully.");
        }else {
            logger.error("Error while creating file");
        }
    }

    @Test
    public void readFileShouldReturnListOfLinesWhenFileExist() throws IOException {
        FileWriter fw = new FileWriter("src/main/resources/" + fileName + ".txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("1 CHF traded at 1.04 times USD");
        bw.newLine();
        bw.write("1 SGD traded at 1.04 times USD");
        bw.flush();
        bw.close();
        logger.info("Appended two lines of text to the file for testing");

        List<String> exchangeRates = exchangeFileReader.readExchangeRate(fileName);
        assertTrue(exchangeRates.size() == 2);
    }

    @Test
    public void readFileShouldReturnEmptyListWhenInputFileIsEmpty() throws IOException {
        List<String> exchangeRates = exchangeFileReader.readExchangeRate(fileName);
        assertTrue(exchangeRates.size() == 0);
    }

    @Test(expected = NoSuchFileException.class)
    public void readFileShouldThrowFileNotFoundExceptionWhenFileNotExist() throws IOException {
        file.delete();
        List<String> exchangeRates = exchangeFileReader.readExchangeRate(fileName);
    }

    @After
    public void tearDown() throws Exception {
        if(file.exists())
            file.delete();
        logger.info("Successfully file deleted.");
    }
}