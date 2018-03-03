package com.burusoth.exchange.cashexchange.cache;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LRUCacheTest {

    @Test
    public void getShouldReturnSavedValueValueIsAlreadyInTheCache() {
        LRUCache lruCache = new LRUCache(10);
        Map<String, Double> rates = new HashMap<>();
        rates.put("SGD", 1.2);
        lruCache.set("28-02-2018", rates);
        assertTrue(lruCache.get("28-02-2018").get("SGD") == 1.2);
    }

    @Test
    public void getShouldReturnNullWhenLRUCacheDoNotHaveValue() {
        LRUCache lruCache = new LRUCache(10);
        assertTrue(lruCache.get("28-02-2018") == null);
    }
}