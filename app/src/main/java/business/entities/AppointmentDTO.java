package business.entities;

public class AppointmentDTO extends BaseDTO{
    private String date; // YYYY-MM-DD
    private String time; // HH:MM
    private long idPatient;
    private long idMedic;
    private StateAppointment state;

    public AppointmentDTO(){
        super();
    }

    public AppointmentDTO(String date, String time, long idPatient, long idMedic, StateAppointment state){
        this.date = date;
        this.time = time;
        this.idPatient = idPatient;
        this.idMedic = idMedic;
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(long idPatient) {
        this.idPatient = idPatient;
    }

    public long getIdMedic() {
        return idMedic;
    }

    public void setIdMedic(long idMedic) {
        this.idMedic = idMedic;
    }

    public StateAppointment getState() {
        return state;
    }

    public void setState(StateAppointment state) {
        this.state = state;
    }

}
