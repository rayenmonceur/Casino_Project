// AuthResponse.java
package tn.casino.casino_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Yetba3eth lel Angular ba3d el login
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}