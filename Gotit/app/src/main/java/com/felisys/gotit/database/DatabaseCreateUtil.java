package com.felisys.gotit.database;

/**
 * Created by gauravkanade on 1/26/17.
 */

public class DatabaseCreateUtil implements DatabaseConstants {

    public static String getCreateQueryForUser() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_USER;
        query += "(";
        query += USR_USER_ID + " VARCHAR(100) PRIMARY KEY, ";
        query += USR_EMAIL_ID + " VARCHAR(200), ";
        query += USR_JSON + " VARCHAR(200000)";
        query += ");";
        return query;

    }

    public static String getCreateQueryForTests() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_TESTS;
        query += "(";
        query += TEST_ID + " VARCHAR(100) PRIMARY KEY, ";
        query += TEST_JSON + " VARCHAR(200000)";
        query += ");";
        return query;
    }

    public static String dropTestTable() {
        return "DROP TABLE IF EXISTS " + TABLE_TESTS;
    }

    public static String dropUsersTable() {
        return "DROP TABLE IF EXISTS " + TABLE_USER;
    }
}
