package business.entities;

public class LoginResponseDTO {
    private String token;
    private String role;  // "MEDIC" o "PATIENT"
    private String email;

    public String getToken()  { return token; }
    public String getRole()   { return role; }
    public String getEmail()  { return email; }
}