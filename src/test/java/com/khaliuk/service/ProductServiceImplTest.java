package com.khaliuk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.khaliuk.dao.ProductDao;
import com.khaliuk.dao.ProductDaoImpl;
import com.khaliuk.model.Product;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    private ProductService productService;
    private Product testProduct;

    @Before
    public void setup() {
        ProductDao productDao = mock(ProductDaoImpl.class);
        productService = new ProductServiceImpl(productDao);
        testProduct = new Product(1L, "iPhone",
                "Apple phone", 1500.0);
        when(productDao.getById(1L)).thenReturn(testProduct);
        when(productDao.getById(2L)).thenReturn(null);
    }

    @Test
    public void testGetByIdSuccessfullyReturnsProductWithId() {
        Optional<Product> expectedResult = Optional.ofNullable(testProduct);
        Optional<Product> actualResult = productService.getById(1L);
        assertEquals(expectedResult.map(Product::getId).get(),
                actualResult.map(Product::getId).get());
    }

    @Test
    public void testGetByIdReturnsOptionalWithNull() {
        Optional<Product> actualResult = productService.getById(2L);
        assertFalse(actualResult.isPresent());
    }
}