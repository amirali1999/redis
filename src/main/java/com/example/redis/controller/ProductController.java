package com.example.redis.controller;

import com.example.redis.dto.ProductDto;
import com.example.redis.model.Product;
import com.example.redis.service.ProductService;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("{id}")
    public Product get(@PathVariable("id") Long id) {
        return productService.get(id);
    }

    @PostMapping
    public Long post(@RequestBody ProductDto.Create create) {
        return productService.post(create);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }

    @PutMapping("{id}")
    public Product put(@PathVariable("id") Long id, @RequestBody ProductDto.Update update) {
        return productService.put(id, update);
    }

}
