package org.javig.micro_adversarial_search.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class GlobalRequest {
    
    @Id
    private long id;
    private int requestCount; // Contador de peticiones global
    private long lastRequestTimestamp; // Timestamp de la última petición

}