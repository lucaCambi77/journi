package com.journi.challenge.models;

/**
 * Represents a Product the company can sell.
 * Id is of course unique.
 * price is always in Euros.
 */
public class Product {

    private final String id;
    private final String description;
    private final Double price;

    private String currencyCode = "EUR";

    public Product(String id, String description, Double price, String currencyCode) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.currencyCode = currencyCode;
    }

    public Product(String id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

}
