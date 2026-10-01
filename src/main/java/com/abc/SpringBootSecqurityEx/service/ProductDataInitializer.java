package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.entity.ProductEntity;
import com.abc.SpringBootSecqurityEx.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(1)
public class ProductDataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        ProductEntity laptop = product("Laptop", "Electronics", "999.99", 10,
                "High-performance laptop", "https://picsum.photos/200", false);
        ProductEntity book = product("Java Programming Book", "Books", "29.99", 50,
                "A practical guide to Java", "https://picsum.photos/201", false);
        ProductEntity premiumLaptop = product("Premium Laptop", "Electronics", "1999.99", 5,
                "Premium high-performance laptop", "https://picsum.photos/300", true);
        productRepository.saveAll(List.of(laptop, book, premiumLaptop));
    }

    private ProductEntity product(String name, String category, String price, int stock,
                                  String description, String imageUrl, boolean premium) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setActive(true);
        product.setPremium(premium);
        return product;
    }
}
