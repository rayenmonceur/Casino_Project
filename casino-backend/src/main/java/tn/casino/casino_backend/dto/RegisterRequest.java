// RegisterRequest.java
package tn.casino.casino_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 20)
    private String username;

    @NotBlank @Email
    private String email;

    // Mot de passe 8 caractères 3al a9all
    @NotBlank @Size(min = 8)
    private String password;
}