package com.khaliuk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.khaliuk.dao.CategoryDao;
import com.khaliuk.dao.CategoryDaoImpl;
import com.khaliuk.model.Category;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    private CategoryService categoryService;
    Category categoryOne;

    @Before
    public void setup() {
        CategoryDao categoryDao = mock(CategoryDaoImpl.class);
        categoryService = new CategoryServiceImpl(categoryDao);
        categoryOne = new Category(1L, "Shoes", "Nice shoes");
        Category categoryTwo = new Category(2L, "Phones", "Mobile phones");
        when(categoryDao.getAll()).thenReturn(Arrays.asList(categoryOne, categoryTwo));
        when(categoryDao.getById(1L)).thenReturn(categoryOne);
        when(categoryDao.getById(3L)).thenReturn(null);
    }

    @Test
    public void testGetAllSuccessfullyReturnsListOfCategories() {
        assertNotNull(categoryService.getAll());
    }

    @Test
    public void testGetByIdSuccessfullyReturnsCategoryWithId() {
        Optional<Category> expectedResult = Optional.ofNullable(categoryOne);
        Optional<Category> actualResult = categoryService.getById(1L);
        assertEquals(expectedResult.map(Category::getId).get(),
                actualResult.map(Category::getId).get());
    }

    @Test
    public void testGetByIdReturnsOptionalWithNull() {
        Optional<Category> actualResult = categoryService.getById(3L);
        assertFalse(actualResult.isPresent());
    }
}