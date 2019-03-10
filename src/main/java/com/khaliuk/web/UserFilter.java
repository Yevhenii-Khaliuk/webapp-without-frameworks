package com.khaliuk.web;

import static com.khaliuk.Factory.*;

import com.khaliuk.Factory;
import com.khaliuk.model.Role;
import com.khaliuk.model.User;
import com.khaliuk.service.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class UserFilter implements Filter {
    private Set<String> openUri = new HashSet<>();
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        openUri.add("/servlet/login");
        openUri.add("/servlet/register");
        userService = getUserServiceImpl(getUserDaoImpl(getConnection()));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        Cookie[] cookies = req.getCookies();

        if (openUri.contains(req.getRequestURI())) {
            processRequest(servletRequest, servletResponse, filterChain);
        } else {
            Optional<User> user = Stream.of(cookies)
                    .filter(c -> c.getName().equals("Mate_Application"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .flatMap(userService::findByToken);

            if (req.getRequestURI().startsWith("/servlet/admin")) {
                boolean isAuthorized = user.map(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getRoleName().equals(Role.RoleName.ADMIN)))
                        .orElse(false);
                        // .filter(r -> r.getRoleName().equals(Role.RoleName.ADMIN))) // old variant
                        // .get().findFirst().isPresent();        //   Variant 1 - working!
                if (isAuthorized) {
                    processRequest(servletRequest, servletResponse, filterChain);
                } else {
                    dispatch(servletRequest, servletResponse, "notAllowed");
                }
            } else {
                if (user.isPresent()) {
                    processRequest(servletRequest, servletResponse, filterChain);
                } else {
                    dispatch(servletRequest, servletResponse, "login");
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

    private void dispatch(ServletRequest servletRequest, ServletResponse servletResponse, String viewName) throws ServletException, IOException {
        servletRequest.getRequestDispatcher(String.format("/WEB-INF/views/%s.jsp", viewName)).forward(servletRequest, servletResponse);
    }

    private void processRequest(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
