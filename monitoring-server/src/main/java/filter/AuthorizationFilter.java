package filter;

import annotations.Loggable;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.user.UserService;
import utils.ServiceFactory;
import utils.Token;

import java.io.IOException;

@Loggable
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/meter/*"})
public class AuthorizationFilter implements Filter {
    private UserService userService;

    public void init(FilterConfig filterConfig) {
        this.userService = ServiceFactory.getUserService();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");
        String path = httpRequest.getServletPath();

        if (!path.equals("/meter/register") && !path.equals("/meter/login")) {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7); // Удаляем "Bearer "
            }
            if (authHeader == null || !userService.isLogin(Token.getUsernameFromToken(authHeader))) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Bad request. Please, Log in");
                return;
            }
        }
        chain.doFilter(request, response);
    }


    public void destroy() {
    }
}