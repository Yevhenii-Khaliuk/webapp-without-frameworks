package com.khaliuk.dao;

import static com.khaliuk.Factory.getConnection;
import static com.khaliuk.Factory.getProductDao;

import com.khaliuk.model.Category;
import com.khaliuk.model.Product;
import java.sql.Connection;
import java.util.List;

public class CategoryDaoImpl extends AbstractDao<Category, Long> implements CategoryDao {
    public CategoryDaoImpl(Connection connection) {
        super(connection);
    }

    public Category getById(Long id) {
        Category category = get(id);
        List<Product> products = getProductDao(getConnection()).getAllByCategoryId(id);
        category.setProducts(products);
        return category;
    }
}
