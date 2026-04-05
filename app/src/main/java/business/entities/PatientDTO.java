package business.entities;

public class PatientDTO extends BaseDTO {

    private String name;
    private String lastName;
    private String dni;
    private String phone;
    private String email;

    public PatientDTO(){
        super();
    }

    public PatientDTO(String name, String lastName, String email, String dni, String phone){
        super();
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.dni = dni;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
