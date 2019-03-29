package com.khaliuk.dao;

import com.khaliuk.model.Product;
import java.util.List;

public interface ProductDao {
    Product getById(Long id);

    List<Product> getAllByCategoryId(Long id);
}
