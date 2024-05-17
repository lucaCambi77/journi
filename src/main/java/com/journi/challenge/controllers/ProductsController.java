package com.journi.challenge.controllers;

import com.journi.challenge.models.Product;
import com.journi.challenge.repositories.ProductsRepository;
import java.util.List;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsController {

    @Inject
    private ProductsRepository productsRepository;

    @GetMapping("/products")
    public List<Product> list(@RequestParam(name = "countryCode", defaultValue = "AT") String countryCode) {
        return productsRepository.list(countryCode);
    }
}
