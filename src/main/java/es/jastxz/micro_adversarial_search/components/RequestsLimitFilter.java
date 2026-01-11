package es.jastxz.micro_adversarial_search.components;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import es.jastxz.micro_adversarial_search.model.GlobalRequest;
import es.jastxz.micro_adversarial_search.model.UserRequest;
import es.jastxz.micro_adversarial_search.repo.GlobalRequestRepo;
import es.jastxz.micro_adversarial_search.repo.UserRequestRepo;

import java.io.IOException;

@Component
public class RequestsLimitFilter implements Filter {

    // Límites para endpoints LIGEROS (ej: health checks, info)
    private static final int MAX_REQUESTS_GLOBAL_LIGHT = 50;
    private static final int MAX_REQUESTS_USER_LIGHT = 15;

    // Límites para endpoints NORMALES (ej: búsquedas simples)
    private static final int MAX_REQUESTS_GLOBAL_NORMAL = 30;
    private static final int MAX_REQUESTS_USER_NORMAL = 7;

    // Límites para endpoints PESADOS (ej: análisis complejos, IA)
    private static final int MAX_REQUESTS_GLOBAL_HEAVY = 15;
    private static final int MAX_REQUESTS_USER_HEAVY = 3;

    private static final long TIME_WINDOW_MILLIS = 10_000; // 10 segundos
    private static final long GLOBAL_ID = 1L;

    private final GlobalRequestRepo globalRequestRepo;
    private final UserRequestRepo userRequestRepo;

    public RequestsLimitFilter(GlobalRequestRepo globalRequestRepo, UserRequestRepo userRequestRepo) {
        this.globalRequestRepo = globalRequestRepo;
        this.userRequestRepo = userRequestRepo;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String endpoint = httpRequest.getRequestURI();
        EndpointType type = determineEndpointType(endpoint);

        String clientId = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        GlobalRequest globalLog = globalRequestRepo.findById(GLOBAL_ID)
                .orElse(createNewGlobalRequest());

        UserRequest userLog = userRequestRepo.findById(clientId)
                .orElse(createNewUserRequest(clientId));

        if (isTimeWindowExpired(globalLog.getLastRequestTimestamp(), currentTime)) {
            globalLog.setRequestCount(0);
            globalLog.setLastRequestTimestamp(currentTime);
        }

        if (isTimeWindowExpired(userLog.getLastRequestTimestamp(), currentTime)) {
            userLog.setRequestCount(0);
            userLog.setLastRequestTimestamp(currentTime);
        }

        globalLog.setRequestCount(globalLog.getRequestCount() + 1);
        userLog.setRequestCount(userLog.getRequestCount() + 1);

        globalLog.setLastRequestTimestamp(currentTime);
        userLog.setLastRequestTimestamp(currentTime);

        // Verificar límites según el tipo de endpoint
        int globalLimit = getGlobalLimit(type);
        int userLimit = getUserLimit(type);

        if (globalLog.getRequestCount() > globalLimit) {
            sendRateLimitResponse(response, "Global rate limit exceeded for " + type + " endpoint");
            return;
        }

        if (userLog.getRequestCount() > userLimit) {
            sendRateLimitResponse(response, "User rate limit exceeded for " + type + " endpoint");
            return;
        }

        globalRequestRepo.save(globalLog);
        userRequestRepo.save(userLog);

        chain.doFilter(request, response);
    }

    private EndpointType determineEndpointType(String endpoint) {
        // Endpoints ligeros
        if (endpoint.matches(".v0/tresenraya")) {
            return EndpointType.LIGHT;
        }

        // Endpoints pesados
        if (endpoint.matches(".v0/damas")) {
            return EndpointType.HEAVY;
        }

        // Por defecto, endpoints normales
        return EndpointType.NORMAL;
    }

    private int getGlobalLimit(EndpointType type) {
        return switch (type) {
            case LIGHT -> MAX_REQUESTS_GLOBAL_LIGHT;
            case NORMAL -> MAX_REQUESTS_GLOBAL_NORMAL;
            case HEAVY -> MAX_REQUESTS_GLOBAL_HEAVY;
        };
    }

    private int getUserLimit(EndpointType type) {
        return switch (type) {
            case LIGHT -> MAX_REQUESTS_USER_LIGHT;
            case NORMAL -> MAX_REQUESTS_USER_NORMAL;
            case HEAVY -> MAX_REQUESTS_USER_HEAVY;
        };
    }

    private boolean isTimeWindowExpired(long lastRequestTime, long currentTime) {
        return lastRequestTime == 0 || (currentTime - lastRequestTime) > TIME_WINDOW_MILLIS;
    }

    private GlobalRequest createNewGlobalRequest() {
        GlobalRequest global = new GlobalRequest();
        global.setId(GLOBAL_ID);
        global.setRequestCount(0);
        global.setLastRequestTimestamp(0);
        return global;
    }

    private UserRequest createNewUserRequest(String clientId) {
        UserRequest user = new UserRequest();
        user.setClientId(clientId);
        user.setRequestCount(0);
        user.setLastRequestTimestamp(0);
        return user;
    }

    private void sendRateLimitResponse(ServletResponse response, String message) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(429);
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write("{\"error\": \"" + message + "\", \"code\": 429}");
    }

    private enum EndpointType {
        LIGHT, // Endpoints ligeros: más peticiones permitidas
        NORMAL, // Endpoints normales: peticiones moderadas
        HEAVY // Endpoints pesados: menos peticiones permitidas
    }
}