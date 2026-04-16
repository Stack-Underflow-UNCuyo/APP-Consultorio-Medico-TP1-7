package business.services;

import android.content.Context;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import business.entities.AppointmentDTO;
import business.entities.StateAppointment;
import business.persistence.AppointmentDAO;

public class AppointmentService {

    private AppointmentDAO appointmentDAO;

    public AppointmentService(Context context) {
        this.appointmentDAO = new AppointmentDAO(context);
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentDAO.getAll();
    }

    public long registerAppointment(String date, String time, String idPatientStr, String idMedicStr) throws Exception {

        if (date == null || date.trim().isEmpty()) throw new Exception("La fecha es obligatoria");
        if (time == null || time.trim().isEmpty()) throw new Exception("La hora es obligatoria");
        if (idPatientStr == null || idPatientStr.trim().isEmpty()) throw new Exception("El ID del paciente es obligatorio");
        if (idMedicStr == null || idMedicStr.trim().isEmpty()) throw new Exception("El ID del médico es obligatorio");

        try {
            LocalDate.parse(date.trim()); // Si falla, va al catch
        } catch (Exception e) {
            throw new Exception("El formato de fecha debe ser YYYY-MM-DD");
        }

        long idPatient;
        long idMedic;
        try {
            idPatient = Long.parseLong(idPatientStr.trim());
            idMedic = Long.parseLong(idMedicStr.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Los IDs deben ser números válidos");
        }

        AppointmentDTO newAppt = new AppointmentDTO(date.trim(), time.trim(), idPatient, idMedic, StateAppointment.PENDING);
        newAppt.setActive(true);

        long id = appointmentDAO.insert(newAppt);
        if (id == -1) {
            throw new Exception("Error al guardar en la base de datos");
        }
        return id;
    }

    public List<AppointmentDTO> getPendingAppointments(List<AppointmentDTO> allAppointments) {
        List<AppointmentDTO> pendingList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (AppointmentDTO appt : allAppointments) {
            try {
                LocalDate apptDate = LocalDate.parse(appt.getDate());
                if (apptDate.isEqual(today) || apptDate.isAfter(today)) {
                    pendingList.add(appt);
                }
            } catch (Exception ignored) {
                // fecha corrupta en la BD, se ignoramos
            }
        }
        return pendingList;
    }
}