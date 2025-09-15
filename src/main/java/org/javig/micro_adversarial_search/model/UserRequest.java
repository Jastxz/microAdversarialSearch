package org.javig.micro_adversarial_search.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserRequest {
    
    @Id
    private String clientId; // Puede ser la IP, un API key, etc.
    private int requestCount; // Contador de peticiones
    private long lastRequestTimestamp; // Timestamp de la última petición

}