package com.khaliuk.controller;

import static java.util.Collections.emptyList;

import com.khaliuk.service.ProductService;
import com.khaliuk.web.Request;
import com.khaliuk.web.ViewModel;

public class GetProductByIdController implements Controller {

    private final ProductService productService;

    public GetProductByIdController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ViewModel process(Request req) {
        if (req.getParam("p_id") == null) {
            return ViewModel.of("404");
        } else {
            String param = req.getParam("p_id")[0];
            try {
                Long id = Long.parseLong(param);
                return productService.getById(id)
                        .map(p -> ViewModel.of("product").withAttribute("product", p))
                        .orElseGet(() -> ViewModel.of("404"));
            } catch (NumberFormatException e) {
                return ViewModel.of("404");
            }
        }
    }
}
