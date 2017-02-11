package com.felisys.gotit.database;

/**
 * Created by gauravkanade on 1/26/17.
 */

public interface DatabaseConstants {
    String DATABASE_SCHEMA = "gotitdb";
    int VERSION = 1;

    //USERS
    String TABLE_USER = "users";
    String USR_USER_ID = "userId";
    String USR_EMAIL_ID = "emailId";
    String USR_JSON = "userJson";

    //TestPapers

    String TABLE_TESTS = "tests";
    String TEST_ID = "testId";
    String TEST_JSON = "testJson";
}
