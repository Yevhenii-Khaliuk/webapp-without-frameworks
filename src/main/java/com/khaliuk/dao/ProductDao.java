package com.khaliuk.dao;

import com.khaliuk.model.Product;

public interface ProductDao {
    Product getById(Long id);
}
