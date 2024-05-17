package com.journi.challenge.repositories;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Product;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class ProductsRepository {

    private final CurrencyConverter currencyConverter = new CurrencyConverter();

    private final List<Product> allProducts = Arrays.asList(new Product("photobook-square-soft-cover", "Photobook Square with Soft Cover", 25.0),
            new Product("photobook-square-hard-cover", "Photobook Square with Hard Cover", 30.0),
            new Product("photobook-landscape-soft-cover", "Photobook Landscape with Soft Cover", 35.0),
            new Product("photobook-landscape-hard-cover", "Photobook Landscape with Hard Cover", 45.0));

    public List<Product> list(String countryCode) {

        String currencyCode = currencyConverter.getCurrencyForCountryCode(countryCode);

        return allProducts.stream().map(p -> new Product(p.getId()
                        , p.getDescription()
                        , currencyConverter.convertEurToCurrency(currencyCode, p.getPrice())
                        , currencyCode))
                .collect(Collectors.toList());
    }
}
