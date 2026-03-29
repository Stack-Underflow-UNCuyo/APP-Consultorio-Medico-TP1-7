package business.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import business.entities.AppointmentDTO;
import business.entities.DTOFactory;
import business.entities.EntityType;

public class AppointmentDAO implements BaseDAO<AppointmentDTO>{

    private SQLiteDatabase db;

    public AppointmentDAO(Context context){
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        this.db = helper.getWritableDatabase();
    }

    @Override
    public long insert(AppointmentDTO appointment){
        ContentValues values = new ContentValues();

        values.put("date", appointment.getDate());
        values.put("time", appointment.getTime());
        values.put("state", appointment.getState().toString());
        values.put("idPatient", appointment.getIdPatient());
        values.put("idMedic", appointment.getIdMedic());
        values.put("active", appointment.isActive() ? 1 : 0);

        return db.insert("appointments", null, values);
    }

    @Override
    public AppointmentDTO getById(long id) {
        AppointmentDTO appointment = null;

        Cursor cursor = db.rawQuery("SELECT * FROM appointments WHERE id = ?", new String[]{ String.valueOf(id) });

        if (cursor.moveToFirst()){
            appointment = (AppointmentDTO) DTOFactory.create(EntityType.APPOINTMENT, cursor);
        }

        cursor.close();

        return appointment;
    }

    @Override
    public List<AppointmentDTO> getAll() {
        List<AppointmentDTO> listAppointments = new ArrayList<>();

        String query = "SELECT * FROM appointments WHERE activo = 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                AppointmentDTO appointment = (AppointmentDTO) DTOFactory.create(EntityType.APPOINTMENT, cursor);

                if (appointment != null) {
                    listAppointments.add(appointment);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        return listAppointments;
    }

    @Override
    public int update(AppointmentDTO appointment) {
        long idToUpdate = appointment.getId();

        ContentValues values = new ContentValues();

        values.put("date", appointment.getDate());
        values.put("time", appointment.getTime());
        values.put("state", appointment.getState().toString());
        values.put("idPatient", appointment.getIdPatient());
        values.put("idMedic", appointment.getIdMedic());
        values.put("active", appointment.isActive() ? 1 : 0);


        return db.update("appointments", values, "id = ? ", new String[]{ String.valueOf(idToUpdate) });
    }

    @Override
    public boolean delete(long id) {
        ContentValues values = new ContentValues();
        values.put("active",  0);
        try{
            db.update("appointments", values, "id = ? ", new String[]{ String.valueOf(id) });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
