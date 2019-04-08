package com.khaliuk.web;

import static com.khaliuk.Factory.getConnection;
import static com.khaliuk.Factory.getUserDaoImpl;
import static com.khaliuk.Factory.getUserServiceImpl;

import com.khaliuk.model.Role;
import com.khaliuk.model.User;
import com.khaliuk.service.UserService;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AdminFilter implements Filter {

    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userService = getUserServiceImpl(getUserDaoImpl(getConnection()));
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        Cookie[] cookies = req.getCookies();

        if (req.getRequestURI().startsWith("/servlet/admin")) {
            if (cookies != null) {
                processRequestCookies(servletRequest, servletResponse, filterChain, cookies);
            } else {
                dispatchNotAllowed(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }

    private void processRequestCookies(ServletRequest servletRequest,
                                       ServletResponse servletResponse,
                                       FilterChain filterChain,
                                       Cookie[] cookies) throws IOException, ServletException {

        Optional<User> user = Stream.of(cookies)
                .filter(c -> c.getName().equals("Mate_Application"))
                .findFirst()
                .map(Cookie::getValue)
                .flatMap(userService::findByToken);
        boolean isAuthorized = user.map(u -> u.getRoles().stream()
                .anyMatch(r -> r.getRoleName().equals(Role.RoleName.ADMIN)))
                .orElse(false);
        if (isAuthorized) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            dispatchNotAllowed(servletRequest, servletResponse);
        }
    }

    private void dispatchNotAllowed(ServletRequest servletRequest,
                                    ServletResponse servletResponse)
            throws ServletException, IOException {
        servletRequest.getRequestDispatcher("/WEB-INF/views/notAllowed.jsp")
                .forward(servletRequest, servletResponse);
    }
}
