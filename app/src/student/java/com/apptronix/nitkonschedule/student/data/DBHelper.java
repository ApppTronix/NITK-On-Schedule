package com.apptronix.nitkonschedule.student.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apptronix.nitkonschedule.student.data.DBContract.AssignmentsEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.RepositoryEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.CourseEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.TestsEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.TimeTableEntry;

/**
 * Created by DevOpsTrends on 12/8/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 22;

    static final String DATABASE_NAME = "student.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TIME_TABLE = "CREATE TABLE " + TimeTableEntry.TABLE_NAME + " (" +
                TimeTableEntry._ID + " INTEGER PRIMARY KEY, " +
                TimeTableEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                TimeTableEntry.COLUMN_COURSE + " TEXT NOT NULL, " +
                TimeTableEntry.COLUMN_PRESENT + " TEXT, " +
                TimeTableEntry.COLUMN_SHORT_NAME + " TEXT, " +
                TimeTableEntry.COLUMN_DESCRIPTION + " TEXT, " +
                TimeTableEntry.COLUMN_DATE + " INTEGER NOT NULL, UNIQUE " +
                "("+ TimeTableEntry.COLUMN_TIME + ", " + TimeTableEntry.COLUMN_DATE + ", " + TimeTableEntry.COLUMN_COURSE + "))";

        final String SQL_CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE " + AssignmentsEntry.TABLE_NAME + " (" +
                AssignmentsEntry._ID + " INTEGER PRIMARY KEY," +
                AssignmentsEntry.COLUMN_COURSE_CODE + " TEXT NOT NULL," +
                AssignmentsEntry.COLUMN_SUBMISSION_DATE + " TEXT NOT NULL," +
                AssignmentsEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                AssignmentsEntry.COLUMN_WEIGHTAGE + " TEXT," +
                AssignmentsEntry.COLUMN_SUBMISSION_TIME + " INTEGER," +
                AssignmentsEntry.COLUMN_SCORE + " DECIMAL," +
                AssignmentsEntry.COLUMN_MAX_SCORE + " DECIMAL," +
                AssignmentsEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, UNIQUE " +
                "("+ AssignmentsEntry.COLUMN_COURSE_CODE + ", " + AssignmentsEntry.COLUMN_TITLE + "))";

        final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + TestsEntry.TABLE_NAME + " (" +
                TestsEntry._ID + " INTEGER PRIMARY KEY," +
                TestsEntry.COLUMN_COURSE_CODE + " TEXT NOT NULL," +
                TestsEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                TestsEntry.COLUMN_SYLLABUS + " TEXT," +
                TestsEntry.COLUMN_SCORE + " DECIMAL," +
                TestsEntry.COLUMN_WEIGHTAGE + " TEXT," +
                TestsEntry.COLUMN_MAX_SCORE + " DECIMAL," +
                TestsEntry.COLUMN_TEST_TIME + " INTEGER," +
                TestsEntry.COLUMN_TEST_DATE + " TEXT NOT NULL, UNIQUE " +
                "("+ TestsEntry.COLUMN_COURSE_CODE + ", " + TestsEntry.COLUMN_TITLE + "))";

        final String SQL_CREATE_COURSE_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME + " (" +
                CourseEntry._ID + " INTEGER PRIMARY KEY," +
                CourseEntry.COLUMN_COURSE_DESCRIPTION + " TEXT," +
                CourseEntry.COLUMN_COURSE_ENROLLED + " TEXT," +
                CourseEntry.COLUMN_ATT_PERCENT + " TEXT," +
                CourseEntry.COLUMN_SEMESTER + " INTEGER," +
                CourseEntry.COLUMN_GRADES + " TEXT," +
                CourseEntry.COLUMN_COURSE + " TEXT NOT NULL, UNIQUE " +
                "("+ CourseEntry.COLUMN_COURSE + "))";

        final String SQL_CREATE_REPOSITORY = "CREATE TABLE " + RepositoryEntry.TABLE_NAME + " (" +
                RepositoryEntry._ID + " INTEGER PRIMARY KEY," +
                RepositoryEntry.COLUMN_COURSE + " TEXT NOT NULL," +
                RepositoryEntry.COLUMN_FILE_LOCATION + " TEXT," +
                RepositoryEntry.COLUMN_FILE_NAME + " TEXT NOT NULL, UNIQUE " +
                "("+ RepositoryEntry.COLUMN_COURSE + "))";

        db.execSQL(SQL_CREATE_TIME_TABLE);
        db.execSQL(SQL_CREATE_ASSIGNMENTS_TABLE);
        db.execSQL(SQL_CREATE_REPOSITORY);
        db.execSQL(SQL_CREATE_TEST_TABLE);
        db.execSQL(SQL_CREATE_COURSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TimeTableEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RepositoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TestsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AssignmentsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        onCreate(db);

    }
}
