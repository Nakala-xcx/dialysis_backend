package com.histsys.data.querys.search;

import lombok.Data;

import java.util.HashMap;

@Data
public class SearchRequest<T> {
    private Integer current = 1;
    private Integer pageSize = 10;
    private T filter; // by view
    private HashMap<String, Order> sort;

    public static enum Order {
        desc,
        asc
    }
}
