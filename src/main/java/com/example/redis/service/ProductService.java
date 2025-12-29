package com.example.redis.service;

import com.example.redis.dto.ProductDto;
import com.example.redis.model.Product;
import com.example.redis.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepository repository;
    private final RedisCacheService redisCacheService;

    public Product get(@NotNull Long id) {

        Optional<Product> cachedProduct = redisCacheService.getJson(String.valueOf(id), Product.class);

        if (cachedProduct.isPresent()) {
            return cachedProduct.get();
        } else {
            Product product = repository.findById(id)
                    .orElseGet(() -> {
                        System.out.println("id not found"); return null; }
                    );

            redisCacheService.cache(String.valueOf(id), product);

            return product;
        }
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);

        redisCacheService.delete(String.valueOf(id));
    }

    public Long post(ProductDto.Create create) {
        Product product = Product.from(create);

        repository.save(product);

        redisCacheService.cache(String.valueOf(product.getId()), product);

        return product.getId();
    }

    public Product put(Long id, ProductDto.Update update) {
        Product product = repository.findById(id).orElseGet(() -> { System.out.println("id not found"); return null;});

        if (product != null && !product.getName().equals(update.getName())) {
            product.setName(update.getName());
        }

        if (product != null && !product.getPrice().equals(update.getPrice())) {
            product.setPrice(update.getPrice());
        }

        if (product != null && !product.getDescription().equals(update.getDescription())) {
            product.setDescription(update.getDescription());
        }

        if (product != null && !product.getQuantity().equals(update.getQuantity())) {
            product.setQuantity(update.getQuantity());
        }

        repository.save(product);

        redisCacheService.delete(String.valueOf(product.getId()));

        return product;
    }
}
