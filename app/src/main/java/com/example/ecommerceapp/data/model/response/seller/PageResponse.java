package com.example.ecommerceapp.data.model.response.seller;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;

    private int number;
    private int size;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;

    private int numberOfElements;

    public List<T> getContent() {
        return content;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }
}