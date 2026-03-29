package business.entities;

public class PatientDTO extends BaseDTO{

    private String name;
    private String lastName;
    private String email;
    private int dni;
    private int phone;

    public PatientDTO(){
        super();
    }

    public PatientDTO(String name, String lastName, String email, int dni, int phone){
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

    public int getDni() { return dni; }
    public void setDni(int dni) { this.dni = dni; }

    public int getPhone() { return phone; }
    public void setPhone(int phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
