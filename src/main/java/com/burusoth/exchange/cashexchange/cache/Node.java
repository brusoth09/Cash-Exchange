package com.burusoth.exchange.cashexchange.cache;

import java.util.Map;

public class Node {
    String key;
    Map<String,Double> value;
    Node pre;
    Node next;

    public Node(String key, Map<String,Double> value){
        this.key = key;
        this.value = value;
    }
}
