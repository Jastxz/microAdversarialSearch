package org.javig.micro_adversarial_search.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int code;
    private String status;
    private String message;
    private long timestamp = System.currentTimeMillis();
    
    public ErrorResponse(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
}