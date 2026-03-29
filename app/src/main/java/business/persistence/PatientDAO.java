package business.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import business.entities.DTOFactory;
import business.entities.EntityType;
import business.entities.PatientDTO;

public class PatientDAO implements BaseDAO<PatientDTO> {
    private SQLiteDatabase db;

    public PatientDAO(Context context){
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        this.db = helper.getWritableDatabase();
    }

    @Override
    public long insert(PatientDTO patient){
        ContentValues values = new ContentValues();

        values.put("name", patient.getName());
        values.put("lastname", patient.getLastName());
        values.put("dni", patient.getDni());
        values.put("phone", patient.getPhone());
        values.put("email", patient.getEmail());
        values.put("active", patient.isActive() ? 1 : 0);

        return db.insert("patients", null, values);
    }

    @Override
    public PatientDTO getById(long id) {
        PatientDTO patient = null;

        Cursor cursor = db.rawQuery("SELECT * FROM patients WHERE id = ?", new String[]{ String.valueOf(id) });

        if (cursor.moveToFirst()){
            patient = (PatientDTO) DTOFactory.create(EntityType.PATIENT, cursor);
        }

        cursor.close();

        return patient;
    }

    @Override
    public List<PatientDTO> getAll() {
        List<PatientDTO> listPatients = new ArrayList<>();

        String query = "SELECT * FROM patients WHERE activo = 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                PatientDTO patient = (PatientDTO) DTOFactory.create(EntityType.PATIENT, cursor);

                if (patient != null) {
                    listPatients.add(patient);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        return listPatients;
    }

    @Override
    public int update(PatientDTO patient) {
        long idToUpdate = patient.getId();

        ContentValues values = new ContentValues();

        values.put("name", patient.getName());
        values.put("lastname", patient.getLastName());
        values.put("dni", patient.getDni());
        values.put("phone", patient.getPhone());
        values.put("email", patient.getEmail());
        values.put("active", patient.isActive() ? 1 : 0);


        return db.update("patients", values, "id = ? ", new String[]{ String.valueOf(idToUpdate) });
    }

    @Override
    public boolean delete(long id) {
        ContentValues values = new ContentValues();
        values.put("active",  0);

        try{
            db.update("patients", values, "id = ? ", new String[]{ String.valueOf(id) });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
