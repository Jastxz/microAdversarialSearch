package org.javig.micro_adversarial_search.components;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.javig.micro_adversarial_search.model.GlobalRequest;
import org.javig.micro_adversarial_search.model.UserRequest;
import org.javig.micro_adversarial_search.repo.GlobalRequestRepo;
import org.javig.micro_adversarial_search.repo.UserRequestRepo;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestsLimitFilter implements Filter {

    private static final int MAX_REQUESTS_GLOBAL_PER_MINUTE = 15;
    private static final int MAX_REQUESTS_USER_PER_MINUTE = 2;
    private static final long TIME_WINDOW_MILLIS = 10_000; // 10 segundos
    private static final long GLOBAL_ID = 1L; // ID fijo para el contador global

    private final GlobalRequestRepo globalRequestRepo;
    private final UserRequestRepo userRequestRepo;

    public RequestsLimitFilter(GlobalRequestRepo globalRequestRepo, UserRequestRepo userRequestRepo) {
        this.globalRequestRepo = globalRequestRepo;
        this.userRequestRepo = userRequestRepo;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String clientId = request.getRemoteAddr(); // Obtener IP del cliente
        long currentTime = System.currentTimeMillis();
        
        // Obtener o crear entidad global con ID fijo
        GlobalRequest globalLog = globalRequestRepo.findById(GLOBAL_ID)
            .orElse(createNewGlobalRequest());
        
        // Obtener o crear entidad del usuario
        UserRequest userLog = userRequestRepo.findById(clientId)
            .orElse(createNewUserRequest(clientId));

        // Verificar y resetear contadores si la ventana de tiempo ha expirado
        if (isTimeWindowExpired(globalLog.getLastRequestTimestamp(), currentTime)) {
            globalLog.setRequestCount(0);
            globalLog.setLastRequestTimestamp(currentTime);
        }
        
        if (isTimeWindowExpired(userLog.getLastRequestTimestamp(), currentTime)) {
            userLog.setRequestCount(0);
            userLog.setLastRequestTimestamp(currentTime);
        }

        // Incrementar contadores
        globalLog.setRequestCount(globalLog.getRequestCount() + 1);
        userLog.setRequestCount(userLog.getRequestCount() + 1);
        
        // Actualizar timestamps
        globalLog.setLastRequestTimestamp(currentTime);
        userLog.setLastRequestTimestamp(currentTime);

        // Verificar límites
        if (globalLog.getRequestCount() > MAX_REQUESTS_GLOBAL_PER_MINUTE) {
            sendRateLimitResponse(response, "Global rate limit exceeded");
            return;
        }
        
        if (userLog.getRequestCount() > MAX_REQUESTS_USER_PER_MINUTE) {
            sendRateLimitResponse(response, "User rate limit exceeded");
            return;
        }

        // Guardar en la base de datos
        globalRequestRepo.save(globalLog);
        userRequestRepo.save(userLog);

        // Continuar con la petición
        chain.doFilter(request, response);
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
        httpResponse.setStatus(429); // Too Many Requests
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write("{\"error\": \"" + message + "\", \"code\": 429}");
    }
}