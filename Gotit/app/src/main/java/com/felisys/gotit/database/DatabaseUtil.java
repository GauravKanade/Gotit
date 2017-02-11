package com.felisys.gotit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.entity.User;
import com.felisys.gotit.util.ObjectMapperUtil;
import com.felisys.gotit.util.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravkanade on 1/26/17.
 */

public class DatabaseUtil implements DatabaseConstants {

    public static void saveUser(SQLiteDatabase sqLiteDatabase, User user) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseUtil.saveUser() -user: " + user.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(USR_USER_ID, user.getUserId());
        contentValues.put(USR_EMAIL_ID, user.getEmailId());
        contentValues.put(USR_JSON, new Gson().toJson(user));
        long status = sqLiteDatabase.insert(TABLE_USER, null, contentValues);
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.saveUser() -status: " + status);
    }

    public static void deleteUser(SQLiteDatabase sqLiteDatabase, String userId) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseUtil.deleteUser() -userId: " + userId);
        long status = sqLiteDatabase.delete(TABLE_USER, USR_USER_ID + "='" + userId + "'", null);
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.deleteUser() -status: " + status);
    }

    public static User getUserById(SQLiteDatabase sqLiteDatabase, String userId) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseUtil.getUserById() -userId: " + userId);
        String rawquery = "SELECT * FROM " + TABLE_USER + " WHERE " + USR_USER_ID + "='" + userId + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(rawquery, null);
        cursor.moveToPosition(-1);
        if (cursor.moveToNext()) {
            String userJSON = cursor.getString(cursor.getColumnIndex(USR_JSON));
            User user = new Gson().fromJson(userJSON, User.class);
            //ObjectMapperUtil.createObjectFromString(userJSON, User.class);
            Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.getUserById() - userJSON: " + userJSON + "user returned = " + user.toString());
            return user;
        }
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.getUserById() - user returned = null");
        return null;
    }

    public static void insertTestPaper(SQLiteDatabase sqLiteDatabase, TestPaper testPaper) {
        Log.i(Utility.DATABASE_LOG_TAG, ">>DatabaseHandler.saveTestPaper - testPaper: " + testPaper);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEST_ID, testPaper.getTestId());
        contentValues.put(TEST_JSON, new Gson().toJson(testPaper));
        long status = sqLiteDatabase.insert(TABLE_TESTS, null, contentValues);
        Log.i(Utility.DATABASE_LOG_TAG, "<<DatabaseHandler.saveTestPaper() - status: " + status);

    }

    public static void deleteTestPaper(SQLiteDatabase sqLiteDatabase, String testId) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseUtil.deleteTestPaper() -testpaperId: " + testId);
        long status = sqLiteDatabase.delete(TABLE_TESTS, TEST_ID + "='" + testId + "'", null);
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.deleteTestPaper() -status: " + status);
    }

    public static TestPaper getTestpaperById(SQLiteDatabase sqLiteDatabase, String testId) {
        Log.d(Utility.DATABASE_LOG_TAG, ">>DatabaseUtil.getTestpaperById() -testiId: " + testId);
        String rawquery = "SELECT * FROM " + TABLE_TESTS + " WHERE " + TEST_ID + "='" + testId + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(rawquery, null);
        cursor.moveToPosition(-1);
        if (cursor.moveToNext()) {
            String testJSON = cursor.getString(cursor.getColumnIndex(TEST_JSON));
            TestPaper testPaper = new Gson().fromJson(testJSON, TestPaper.class);
            Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.getTestpaperById() - testpaper returned = " + testPaper.toString());
            return testPaper;
        }
        Log.d(Utility.DATABASE_LOG_TAG, "<<DatabaseUtil.getTestpaperById() - testpaper returned = null");
        return null;
    }

    public static List<TestPaper> getAllTestPapers(SQLiteDatabase sqLiteDatabase) {
        Log.d(Utility.LOG_TAG, ">>DatabaseUtil.getAllTestPapers()");
        List<TestPaper> testPaperList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TESTS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToPosition(-1);
        Gson gson = new Gson();
        while (cursor.moveToNext()) {
            String testJSON = cursor.getString(cursor.getColumnIndex(TEST_JSON));
            TestPaper testPaper = gson.fromJson(testJSON, TestPaper.class);
            testPaperList.add(testPaper);
        }
        Log.d(Utility.LOG_TAG, "<<DatabaseUtil.getAllTestPapers() - " + testPaperList);
        return testPaperList;
    }
}
