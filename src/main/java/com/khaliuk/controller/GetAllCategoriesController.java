package com.khaliuk.controller;

import com.khaliuk.service.CategoryService;
import com.khaliuk.web.Request;
import com.khaliuk.web.ViewModel;

public class GetAllCategoriesController implements Controller {

    private final CategoryService categoryService;

    public GetAllCategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public ViewModel process(Request req) {
        return ViewModel.of("categories").withAttribute("categories", categoryService.getAll());
    }
}
