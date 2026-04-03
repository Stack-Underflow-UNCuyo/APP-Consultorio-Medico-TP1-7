package business.entities;

import android.database.Cursor;

public class DTOFactory {

    public static BaseDTO create(EntityType type, Cursor cursor){

        // cursor is null
        if (cursor == null || cursor.isClosed() || cursor.getCount() == 0){
            return  null;
        }

        switch (type){
            case PATIENT:
                return ensamblerPatient(cursor);

            case MEDIC:
                return ensamblerMedic(cursor);

            case APPOINTMENT:
                return ensamblerAppointment(cursor);

            default:
                throw new IllegalArgumentException("Tipo de entidad no soportado: " + type);
        }
    }

    private static PatientDTO ensamblerPatient(Cursor cursor) {
        PatientDTO patient = new PatientDTO();

        // get column index
        int indexId = cursor.getColumnIndex("id");
        int indexName = cursor.getColumnIndex("name");
        int indexLastName = cursor.getColumnIndex("lastname");
        int indexDni = cursor.getColumnIndex("dni");
        int indexEmail = cursor.getColumnIndex("email");
        int indexPhone = cursor.getColumnIndex("phone");

        // get & set data
        if (indexId >= 0) { patient.setId(cursor.getLong(indexId));}
        if (indexName >= 0) {patient.setName(cursor.getString(indexName));}
        if (indexLastName >= 0) {patient.setName(cursor.getString(indexLastName));}
        if (indexDni >= 0) {patient.setName(cursor.getString(indexDni));}
        if (indexEmail >= 0) {patient.setName(cursor.getString(indexEmail));}
        if (indexPhone >= 0) {patient.setName(cursor.getString(indexPhone));}

        return patient;
    }

    private static MedicDTO ensamblerMedic(Cursor cursor) {
        MedicDTO medic = new MedicDTO();

        // get column index
        int indexId = cursor.getColumnIndex("id");
        int indexName = cursor.getColumnIndex("name");
        int indexLastName = cursor.getColumnIndex("lastname");
        int indexRegistration = cursor.getColumnIndex("registration");
        int indexSpeciality = cursor.getColumnIndex("speciality");

        // get & set data
        if (indexId >= 0) { medic.setId(cursor.getLong(indexId));}
        if (indexName >= 0) {medic.setName(cursor.getString(indexName));}
        if (indexLastName >= 0) {medic.setName(cursor.getString(indexLastName));}
        if (indexRegistration >= 0) {medic.setName(cursor.getString(indexRegistration));}
        if (indexSpeciality >= 0) {medic.setName(cursor.getString(indexSpeciality));}

        return medic;
    }

    private static AppointmentDTO ensamblerAppointment(Cursor cursor) {
        AppointmentDTO appointment = new AppointmentDTO();

        // get column index
        int indexId = cursor.getColumnIndex("id");
        int indexDate = cursor.getColumnIndex("date");
        int indexTime = cursor.getColumnIndex("time");
        int indexIdPatient = cursor.getColumnIndex("idPatient");
        int indexIdMedic = cursor.getColumnIndex("idMedic");
        int indexState = cursor.getColumnIndex("state");

        // get & set data
        if (indexId >= 0) { appointment.setId(cursor.getLong(indexId));}
        if (indexDate >= 0) {appointment.setDate(cursor.getString(indexDate));}
        if (indexTime >= 0) {appointment.setTime(cursor.getString(indexTime));}
        if (indexIdPatient >= 0) {appointment.setIdPatient(cursor.getLong(indexIdPatient));}
        if (indexIdMedic >= 0) {appointment.setIdMedic(cursor.getLong(indexIdMedic));}
        if (indexState >= 0) {appointment.setState(StateAppointment.valueOf(cursor.getString(indexState)));}

        return appointment;
    }
}
