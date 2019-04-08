package com.khaliuk.controller;

import static java.util.Collections.emptyList;

import com.khaliuk.service.CategoryService;
import com.khaliuk.web.Request;
import com.khaliuk.web.ViewModel;

public class GetCategoryByIdController implements Controller {

    private final CategoryService categoryService;

    public GetCategoryByIdController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public ViewModel process(Request req) {
        if (req.getParam("c_id") == null) {
            return ViewModel.of("404");
        } else {
            String param = req.getParam("c_id")[0];
            try {
                Long id = Long.parseLong(param);
                return categoryService.getById(id)
                        .map(c -> ViewModel.of("category").withAttribute("category", c))
                        .orElseGet(() -> ViewModel.of("404"));
            } catch (NumberFormatException e) {
                return ViewModel.of("404");
            }
        }
    }
}
