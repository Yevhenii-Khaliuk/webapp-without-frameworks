package com.khaliuk.dao;

import com.khaliuk.model.Category;
import java.util.List;

public interface CategoryDao {
    Category getById(Long id);

    List<Category> getAll();
}
