package com.khaliuk.web;

import static com.khaliuk.Factory.getCategoryDao;
import static com.khaliuk.Factory.getCategoryService;
import static com.khaliuk.Factory.getConnection;
import static com.khaliuk.Factory.getGetAllCategoriesController;
import static com.khaliuk.Factory.getGetCategoryByIdController;
import static com.khaliuk.Factory.getGetProductByIdController;
import static com.khaliuk.Factory.getLoginUserController;
import static com.khaliuk.Factory.getProductDao;
import static com.khaliuk.Factory.getProductService;
import static com.khaliuk.Factory.getUserDaoImpl;
import static com.khaliuk.Factory.getUserServiceImpl;

import com.khaliuk.controller.Controller;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MainServlet extends HttpServlet {
    private static final Map<Request, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put(Request.of("GET", "/servlet/login"),
                r -> ViewModel.of("login"));
        controllerMap.put(Request.of("POST", "/servlet/login"),
                getLoginUserController(getUserServiceImpl(getUserDaoImpl(getConnection()))));
        controllerMap.put(Request.of("GET", "/servlet/categories"),
                getGetAllCategoriesController(getCategoryService(getCategoryDao(getConnection()))));
        controllerMap.put(Request.of("GET", "/servlet/category"),
                getGetCategoryByIdController(getCategoryService(getCategoryDao(getConnection()))));
        controllerMap.put(Request.of("GET", "/servlet/admin"),
                r -> ViewModel.of("admin"));
        controllerMap.put(Request.of("GET", "/servlet/product"),
                getGetProductByIdController(getProductService(getProductDao(getConnection()))));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Request request = Request.of(req.getMethod(), req.getRequestURI(), req.getParameterMap());
        Controller controller = controllerMap.getOrDefault(request, r -> ViewModel.of("404"));
        ViewModel vm = controller.process(request);
        processViewModel(vm, req, resp);
    }

    private void processViewModel(ViewModel vm, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        vm.getAttributes().forEach(req::setAttribute);
        processCookies(vm.getCookies(), resp);
        req.getRequestDispatcher(vm.getRedirectUri()).forward(req, resp);
    }

    private void processCookies(List<Cookie> cookie, HttpServletResponse resp) {
        cookie.forEach(c ->
                resp.addCookie(new javax.servlet.http.Cookie(c.getName(), c.getValue())));
    }
}
