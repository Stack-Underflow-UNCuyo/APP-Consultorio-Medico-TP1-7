package business.services;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import business.entities.PatientDTO;
import business.persistence.PatientDAO;

public class PatientService {

    private PatientDAO patientDAO;

    public PatientService(Context context) {
        this.patientDAO = new PatientDAO(context);
    }

    public List<PatientDTO> getAllPatients() {
        return patientDAO.getAll();
    }

    public long registerPatient(String name, String lastName, String dniStr, String phoneStr, String email) throws Exception {

        if (name == null || name.trim().isEmpty()) throw new Exception("El nombre es obligatorio");
        if (lastName == null || lastName.trim().isEmpty()) throw new Exception("El apellido es obligatorio");

        if (dniStr == null || dniStr.trim().isEmpty()) throw new Exception("El DNI es obligatorio");
        if (dniStr.length() < 7 || dniStr.length() > 8) throw new Exception("DNI inválido (7 u 8 dígitos)");

        if (phoneStr == null || phoneStr.trim().isEmpty()) throw new Exception("El teléfono es obligatorio");

        if (email == null || email.trim().isEmpty()) throw new Exception("El email es obligatorio");
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) throw new Exception("Formato de email inválido");

        int dni = Integer.parseInt(dniStr.trim());

        PatientDTO newPatient = new PatientDTO(name.trim(), lastName.trim(), email.trim(), dni, phoneStr);
        newPatient.setActive(true);

        long id = patientDAO.insert(newPatient);
        if (id == -1) {
            throw new Exception("Error interno al guardar en la base de datos");
        }
        return id;
    }

    public List<PatientDTO> filterPatients(List<PatientDTO> allPatients, String searchText) {
        if (allPatients == null) return new ArrayList<>();

        List<PatientDTO> filteredList = new ArrayList<>();
        String searchLower = searchText != null ? searchText.toLowerCase().trim() : "";

        for (PatientDTO patient : allPatients) {
            if (patient.getName().toLowerCase().contains(searchLower) ||
                    patient.getLastName().toLowerCase().contains(searchLower) ||
                    String.valueOf(patient.getDni()).contains(searchLower)) {
                filteredList.add(patient);
            }
        }
        return filteredList;
    }
}