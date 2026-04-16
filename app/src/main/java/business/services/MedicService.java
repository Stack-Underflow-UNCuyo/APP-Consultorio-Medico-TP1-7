package business.services;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import business.entities.MedicDTO;
import business.persistence.MedicDAO;

public class MedicService {

    private MedicDAO medicDAO;

    public MedicService(Context context) {
        this.medicDAO = new MedicDAO(context);
    }

    public List<MedicDTO> getAllMedics() {
        return medicDAO.getAll();
    }

    public long registerMedic(String name, String lastName, String registration, String speciality) throws Exception {

        // Reglas de negocio y validaciones
        if (name == null || name.trim().isEmpty()) throw new Exception("El nombre es obligatorio");
        if (lastName == null || lastName.trim().isEmpty()) throw new Exception("El apellido es obligatorio");
        if (registration == null || registration.trim().isEmpty()) throw new Exception("La matrícula es obligatoria");
        if (speciality == null || speciality.trim().isEmpty()) throw new Exception("La especialidad es obligatoria");

        MedicDTO newMedic = new MedicDTO(name.trim(), lastName.trim(), registration.trim(), speciality.trim());
        newMedic.setActive(true);

        long id = medicDAO.insert(newMedic);
        if (id == -1) {
            throw new Exception("Error interno al guardar en la base de datos");
        }
        return id;
    }

    public List<MedicDTO> filterMedics(List<MedicDTO> allMedics, String searchText, String specialityFilter) {
        List<MedicDTO> filteredList = new ArrayList<>();
        String searchLower = searchText != null ? searchText.toLowerCase().trim() : "";

        for (MedicDTO medic : allMedics) {
            boolean matchesText = searchLower.isEmpty() ||
                    medic.getName().toLowerCase().contains(searchLower) ||
                    medic.getLastName().toLowerCase().contains(searchLower) ||
                    medic.getSpeciality().toLowerCase().contains(searchLower);

            boolean matchesSpeciality = specialityFilter.equals("Todos") ||
                    medic.getSpeciality().equalsIgnoreCase(specialityFilter);

            if (matchesText && matchesSpeciality) {
                filteredList.add(medic);
            }
        }
        return filteredList;
    }
}