package com.example.bookshop.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Root {
    @JsonProperty("kind")
    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    String kind;

    @JsonProperty("totalItems")
    public int getTotalItems() {
        return this.totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    int totalItems;

    @JsonProperty("items")
    public ArrayList<Item> getItems() {
        return this.items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    ArrayList<Item> items;
}
