package business.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import business.entities.DTOFactory;
import business.entities.EntityType;
import business.entities.MedicDTO;

public class MedicDAO implements BaseDAO<MedicDTO> {
    private SQLiteDatabase db;

    public MedicDAO(Context context){
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        this.db = helper.getWritableDatabase();
    }

    @Override
    public long insert(MedicDTO medic){
        ContentValues values = new ContentValues();

        values.put("name", medic.getName());
        values.put("lastname", medic.getLastName());
        values.put("registration", medic.getRegistration());
        values.put("speciality", medic.getSpeciality());
        values.put("active", medic.isActive() ? 1 : 0);

        return db.insert("medics", null, values);
    }

    @Override
    public MedicDTO getById(long id) {
        MedicDTO medic = null;



        Cursor cursor = db.rawQuery("SELECT * FROM medics WHERE id = ?", new String[]{ String.valueOf(id) });

        if (cursor.moveToFirst()){
            medic = (MedicDTO) DTOFactory.create(EntityType.MEDIC, cursor);
        }

        cursor.close();

        return medic;
    }

    @Override
    public List<MedicDTO> getAll() {
        List<MedicDTO> listMedics = new ArrayList<>();

        String query = "SELECT * FROM pacients WHERE activo = 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                MedicDTO medic = (MedicDTO) DTOFactory.create(EntityType.MEDIC, cursor);

                if (medic != null) {
                    listMedics.add(medic);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        return listMedics;
    }

    @Override
    public int update(MedicDTO medic) {
        long idToUpdate = medic.getId();

        ContentValues values = new ContentValues();

        values.put("name", medic.getName());
        values.put("lastname", medic.getLastName());
        values.put("registration", medic.getRegistration());
        values.put("speciality", medic.getSpeciality());
        values.put("active", medic.isActive() ? 1 : 0);


        return db.update("medics", values, "id = ? ", new String[]{ String.valueOf(idToUpdate) });
    }

    @Override
    public boolean delete(long id) {
        ContentValues values = new ContentValues();
        values.put("active",  0);

        try{
            db.update("medics", values, "id = ? ", new String[]{ String.valueOf(id) });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
