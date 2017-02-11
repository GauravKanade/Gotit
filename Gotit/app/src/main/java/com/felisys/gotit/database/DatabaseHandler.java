package com.felisys.gotit.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.entity.User;
import com.felisys.gotit.util.Utility;

import java.util.List;

/**
 * Created by gauravkanade on 1/26/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper implements DatabaseConstants {
    Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_SCHEMA, null, VERSION);
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseHandler Constructor");
        SQLiteDatabase database = this.getWritableDatabase();
        //onUpgrade(database, VERSION, VERSION);
        onCreate(database);
        Log.d(Utility.DATABASE_LOG_TAG, "database: " + database.toString());
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseHandler.onCreate");
        try {
            db.execSQL(DatabaseCreateUtil.getCreateQueryForTests());
            db.execSQL(DatabaseCreateUtil.getCreateQueryForUser());
        } catch (SQLException e) {
            Log.e(Utility.DATABASE_LOG_TAG, "Error in creating DB", e);
        }
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseHandler.onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseHandler.onUpdate");
        try {
            db.execSQL(DatabaseCreateUtil.dropTestTable());
            db.execSQL(DatabaseCreateUtil.dropUsersTable());

            db.execSQL(DatabaseCreateUtil.getCreateQueryForTests());
            db.execSQL(DatabaseCreateUtil.getCreateQueryForUser());
        } catch (SQLException e) {
            Log.e(Utility.DATABASE_LOG_TAG, "Error in creating DB", e);
        }
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseHandler.onUpdate");
    }

    public void saveUser(User user) {
        DatabaseUtil.deleteUser(this.getWritableDatabase(), user.getUserId());
        DatabaseUtil.saveUser(this.getWritableDatabase(), user);
    }

    public void deleteUser(String userId) {
        DatabaseUtil.deleteUser(this.getWritableDatabase(), userId);
    }

    public User getUserById(String userId) {
        return DatabaseUtil.getUserById(this.getReadableDatabase(), userId);
    }

    public void saveTestPaper(TestPaper testPaper) {
        DatabaseUtil.deleteTestPaper(this.getWritableDatabase(), testPaper.getTestId());
        DatabaseUtil.insertTestPaper(this.getWritableDatabase(), testPaper);
    }

    public TestPaper getTestPaperById(String testId) {
        return DatabaseUtil.getTestpaperById(this.getReadableDatabase(), testId);
    }

    public List<TestPaper> getAllTestPapers() {
        return DatabaseUtil.getAllTestPapers(this.getReadableDatabase());
    }
}
