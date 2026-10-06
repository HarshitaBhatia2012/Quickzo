package com.MiniProject.backend.repository;

import com.MiniProject.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface ProductRepository extends JpaRepository<Product, Long> {
    }

