package com.khaliuk.service;

import com.khaliuk.model.Product;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getById(Long id);
}
