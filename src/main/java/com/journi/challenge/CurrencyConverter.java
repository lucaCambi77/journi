package com.journi.challenge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CurrencyConverter {
    private final Map<String, String> supportedCountriesCurrency;
    private final Map<String, Double> currencyEurRate;

    public CurrencyConverter() {
        supportedCountriesCurrency = new HashMap<>();
        supportedCountriesCurrency.put("AT", "EUR");
        supportedCountriesCurrency.put("DE", "EUR");
        supportedCountriesCurrency.put("HU", "HUF");
        supportedCountriesCurrency.put("GB", "GBP");
        supportedCountriesCurrency.put("FR", "EUR");
        supportedCountriesCurrency.put("PT", "EUR");
        supportedCountriesCurrency.put("IE", "EUR");
        supportedCountriesCurrency.put("ES", "EUR");
        supportedCountriesCurrency.put("BR", "BRL");
        supportedCountriesCurrency.put("US", "USD");
        supportedCountriesCurrency.put("CA", "CAD");

        currencyEurRate = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            URL rates = getClass().getResource("/eur_rate.json");
            JsonNode ratesTree = mapper.readTree(rates);
            Iterator<JsonNode> currenciesIterator = ratesTree.findPath("currencies").elements();
            currenciesIterator.forEachRemaining(currency -> currencyEurRate.put(currency.get("currency").asText(), currency.get("rate").asDouble()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getCurrencyForCountryCode(String countryCode) {
        return supportedCountriesCurrency.getOrDefault(countryCode.toUpperCase(), "EUR");
    }

    public Double convertEurToCurrency(String currencyCode, Double eurValue) {
        return eurValue * currencyEurRate.getOrDefault(currencyCode, 1.0);
    }
}
