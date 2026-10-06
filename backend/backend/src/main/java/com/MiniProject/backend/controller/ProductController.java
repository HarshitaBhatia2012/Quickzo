package com.MiniProject.backend.controller;
import com.MiniProject.backend.model.Product;
import com.MiniProject.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
            this.productService = productService;
        }

        @GetMapping
        public List<Product> getAllProducts() {
            return productService.getAllProducts();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Product> getProductById(@PathVariable Long id) {
            Product product = productService.getProductById(id);
            return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
        }

        @PostMapping
        public ResponseEntity<Product> createProduct(@RequestBody Product product) {
            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct != null) {
                product.setId(id);
                Product updatedProduct = productService.saveProduct(product);
                return ResponseEntity.ok(updatedProduct);
            }
            return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
    }
