package com.br.strudylab.aws_course_project01.controller;

import com.br.strudylab.aws_course_project01.enums.EventType;
import com.br.strudylab.aws_course_project01.model.Product;
import com.br.strudylab.aws_course_project01.repository.ProductRepository;
import com.br.strudylab.aws_course_project01.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private ProductRepository productRepository;
    private ProductPublisher productPublisher;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductPublisher productPublisher) {
        this.productRepository = productRepository;
        this.productPublisher = productPublisher;
    }

    @GetMapping
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = productRepository.save(product);
        productPublisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "nayron");
        return new ResponseEntity<Product>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(record -> {
                    record.setName(product.getName());
                    record.setModel(product.getModel());
                    record.setCode(product.getCode());
                    record.setPrice(product.getPrice());
                    Product updated = productRepository.save(record);
                    productPublisher.publishProductEvent(product, EventType.PRODUCT_UPDATED, "May");
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(record -> {
                    productRepository.deleteById(id);
                    productPublisher.publishProductEvent(record, EventType.PRODUCT_DELETED, "Oliver");
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bycode/{code}")
    public ResponseEntity<Product> findByCode(@PathVariable String code) {
        return productRepository.findByCode(code)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

}
