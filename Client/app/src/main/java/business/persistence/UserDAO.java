package business.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import business.entities.UserDTO;

public class UserDAO implements BaseDAO<UserDTO> {

    private SQLiteDatabase db;

    public UserDAO(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        this.db = helper.getWritableDatabase();
    }

    @Override
    public long insert(UserDTO user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("lastname", user.getLastName());
        values.put("dni", user.getDni());
        values.put("phone", user.getPhone());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("active", user.isActive() ? 1 : 0);
        return db.insert("users", null, values);
    }

    @Override
    public UserDTO getById(long id) {
        Cursor cursor = db.query("users", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            UserDTO user = mapCursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    @Override
    public List<UserDTO> getAll() {
        List<UserDTO> users = new ArrayList<>();
        Cursor cursor = db.query("users", null, "active = 1", null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                users.add(mapCursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    @Override
    public int update(UserDTO user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("lastname", user.getLastName());
        values.put("dni", user.getDni());
        values.put("phone", user.getPhone());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("active", user.isActive() ? 1 : 0);
        return db.update("users", values, "id = ?", new String[]{String.valueOf(user.getId())});
    }

    @Override
    public boolean delete(long id) {
        ContentValues values = new ContentValues();
        values.put("active", 0);
        return db.update("users", values, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public UserDTO getByEmail(String email) {
        Cursor cursor = db.query("users", null, "email = ? AND active = 1", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            UserDTO user = mapCursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    private UserDTO mapCursorToUser(Cursor cursor) {
        UserDTO user = new UserDTO();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("lastname")));
        user.setDni(cursor.getString(cursor.getColumnIndexOrThrow("dni")));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("active")) == 1);
        return user;
    }
}
