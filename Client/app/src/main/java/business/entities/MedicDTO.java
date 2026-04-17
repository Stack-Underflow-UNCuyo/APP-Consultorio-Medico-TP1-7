package business.entities;

public class MedicDTO extends BaseDTO{
    private String name;
    private String lastName;

    private String registration;
    private String speciality;

    public MedicDTO(){
        super();
    }

    public MedicDTO(String name, String lastName, String registration, String speciality){
        super();
        this.name = name;
        this.lastName = lastName;
        this.registration = registration;
        this.speciality = speciality;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRegistration() {return  registration;}

    public void  setRegistration(String registration) {this.registration = registration;}

    public String getSpeciality() { return speciality; }

    public void setSpeciality(String speciality) {this.speciality = speciality; }
}
