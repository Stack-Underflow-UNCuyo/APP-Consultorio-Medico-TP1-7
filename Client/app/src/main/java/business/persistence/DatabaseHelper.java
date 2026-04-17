package business.persistence;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ConsultorioDB.db";
    private static final int DATABASE_VERSION = 4; // Incremented for medic email

    private static DatabaseHelper instance;

    private static final String CREATE_TABLE_PATIENT =
            "CREATE TABLE patients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "lastname TEXT, " +
                    "dni TEXT, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "active INTEGER)";

    private static final String CREATE_TABLE_MEDIC =
            "CREATE TABLE medics (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "lastname TEXT, " +
                    "registration TEXT, " +
                    "speciality TEXT, " +
                    "email TEXT, " + // Added email for medics
                    "active INTEGER)";

    private static final String CREATE_TABLE_APPOINTMENTS =
            "CREATE TABLE appointments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "date TEXT, " +
                    "time TEXT, " +
                    "state TEXT, " +
                    "id_medic INTEGER, " +
                    "id_patient INTEGER, " +
                    "active INTEGER, " +
                    "FOREIGN KEY(id_patient) REFERENCES patients(id), " +
                    "FOREIGN KEY(id_medic) REFERENCES medics(id))";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "lastname TEXT, " +
                    "dni TEXT, " +
                    "phone TEXT, " +
                    "email TEXT, " +
                    "password TEXT, " +
                    "active INTEGER)";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS appointments");
        db.execSQL("DROP TABLE IF EXISTS medics");
        db.execSQL("DROP TABLE IF EXISTS patients");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PATIENT);
        db.execSQL(CREATE_TABLE_MEDIC);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
        db.execSQL(CREATE_TABLE_USERS);
    }
}
