package com.example.redis.service;

import com.example.redis.config.cache.CacheKeys;
import com.example.redis.dto.PagedResult;
import com.example.redis.dto.ProductDto;
import com.example.redis.model.Product;
import com.example.redis.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepository repository;
    private final RedisCacheService cache;

    public Product get(@NotNull Long id) {

        Optional<Product> cachedProduct = cache.getJson(String.valueOf(id), Product.class);

        if (cachedProduct.isPresent()) {
            return cachedProduct.get();
        } else {
            Product product = repository.findById(id)
                    .orElseGet(() -> {
                        System.out.println("id not found"); return null; }
                    );

            cache.cache(String.valueOf(id), product);

            return product;
        }
    }

    public PagedResult<Product> getAll(int page, int size, String sort) {
        long ver = cache.getLongOrDefault(CacheKeys.PRODUCT_LIST_VER, 1);
        String key = CacheKeys.listKey(ver, page, size, sort);

        Optional<PagedResult> cached = cache.getJson(key, PagedResult.class);
        if (cached.isPresent()) {
            return (PagedResult<Product>) cached.get();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> db = repository.findAll(pageable);

        PagedResult<Product> result = new PagedResult<>(
                db.getContent(), page, size, db.getTotalElements(), db.getTotalPages()
        );

        cache.cache(key, result);
        return result;
    }
    public void delete(Long id) {
        repository.deleteById(id);

        cache.delete(String.valueOf(id));
    }

    public Long post(ProductDto.Create create) {
        Product product = Product.from(create);

        repository.save(product);

        cache.cache(String.valueOf(product.getId()), product);

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

        cache.delete(String.valueOf(product.getId()));

        return product;
    }
}
