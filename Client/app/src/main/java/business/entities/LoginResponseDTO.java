package business.entities;

public class LoginResponseDTO {
    private String token;
    private String role;  // "MEDIC" o "PATIENT"
    private String email;
    private String name;
    private Long id; // Added id to store the user's patient or medic ID

    public String getToken()  { return token; }
    public String getRole()   { return role; }
    public String getEmail()  { return email; }
    public String getName()   { return name; }
    public Long getId()       { return id; }
}
