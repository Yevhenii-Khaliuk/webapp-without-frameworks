package com.khaliuk.service;

import com.khaliuk.dao.ProductDao;
import com.khaliuk.model.Product;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Optional<Product> getById(Long id) {
        return Optional.ofNullable(productDao.getById(id));
    }
}
