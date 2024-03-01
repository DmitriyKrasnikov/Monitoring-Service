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

/**
 * Фильтр авторизации.
 * Этот класс реализует интерфейс Filter и предоставляет методы для проверки авторизации пользователя.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 * @WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/meter/*"}) Аннотация, указывающая, что этот класс является фильтром веб-запросов.
 */
@Loggable
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/meter/*"})
public class AuthorizationFilter implements Filter {
    private UserService userService;

    /**
     * Инициализация фильтра.
     *
     * @param filterConfig Объект FilterConfig, содержащий конфигурацию фильтра.
     */
    public void init(FilterConfig filterConfig) {
        this.userService = ServiceFactory.getUserService();
    }

    /**
     * Выполнение фильтрации.
     *
     * @param request Объект ServletRequest, содержащий запрос клиента.
     * @param response Объект ServletResponse, содержащий ответ сервера.
     * @param chain Объект FilterChain, содержащий цепочку фильтров.
     * @throws IOException В случае ошибки ввода/вывода.
     * @throws ServletException В случае ошибки сервлета.
     */
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