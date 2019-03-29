package com.khaliuk.dao;

import com.khaliuk.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl extends AbstractDao<Product, Long> implements ProductDao {
    public ProductDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Product getById(Long id) {
        return get(id);
    }

    public List<Product> getAllByCategoryId(Long id) {
        String query = "SELECT P.ID, " +
                        "P.PRODUCT_NAME, " +
                        "P.PRODUCT_DESCRIPTION, " +
                        "P.PRICE " +
                        "FROM PRODUCTS P " +
                        "WHERE P.FK_CATEGORY_ID = ?";
        List<Product> products = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getLong("ID"));
                product.setProductName(resultSet.getString("PRODUCT_NAME"));
                product.setProductDescription(
                        resultSet.getString("PRODUCT_DESCRIPTION"));
                product.setPrice(resultSet.getDouble("PRICE"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
