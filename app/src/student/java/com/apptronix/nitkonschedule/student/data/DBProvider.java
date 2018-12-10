package com.apptronix.nitkonschedule.student.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.apptronix.nitkonschedule.student.data.DBContract.AssignmentsEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.CourseEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.RepositoryEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.TestsEntry;
import com.apptronix.nitkonschedule.student.data.DBContract.TimeTableEntry;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created by DevOpsTrends on 12/8/2016.
 */

public class DBProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    static final int TIME_TABLE = 100;
    static final int ASSIGNMENTS = 101;
    static final int TESTS = 102;
    static final int ATTENDANCE = 103;
    static final int TIME_TABLE_DAY = 104;
    static final int ATTENDANCE_PERCENTAGE = 105;
    static final int TIME_TABLE_WITH_ATTENDANCE = 106;
    static final int COURSES = 107;
    static final int TIMETABLE_MAX_DATE = 110;
    static final int TIMETABLE_MIN_DATE = 111;

    private static final SQLiteQueryBuilder sStudentQueryBuilder;

    private SQLiteDatabase db;

    static{

        sStudentQueryBuilder = new SQLiteQueryBuilder();

        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(TestsEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(AssignmentsEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(RepositoryEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(CourseEntry.TABLE_NAME);

    }

    private Cursor getTimeTable(Uri uri) {

        String day = TimeTableEntry.getTimeTableDay(uri);
        Timber.i("provider %s", day);

        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                TimeTableEntry.TABLE_NAME +"."+ TimeTableEntry.COLUMN_DATE + " = ? ",
                new String[]{day},
                null,
                null,
                null
        );

    }


    private Cursor getTests(Uri uri) {
        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(TestsEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }

    private Cursor getAttendancePercentages(Uri uri) {


        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(CourseEntry.TABLE_NAME);

        return sStudentQueryBuilder.query(db,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    private Cursor getTimeTableWithAttendance(Uri uri) {
        String day = TimeTableEntry.getTimeTableDay(uri);

        db=mOpenHelper.getReadableDatabase();

        final String MY_QUERY = "SELECT * FROM " + TimeTableEntry.TABLE_NAME + " a INNER JOIN " + CourseEntry.TABLE_NAME +
                " b ON a." + TimeTableEntry.COLUMN_COURSE + "=b."+ CourseEntry.COLUMN_COURSE+ " WHERE a." +
                TimeTableEntry.COLUMN_DATE+"=?";
        Timber.i("query is %s",MY_QUERY);
        return db.rawQuery(MY_QUERY, new String[]{String.valueOf(day)});

    }



    private Cursor getAssignments(Uri uri) {

        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(AssignmentsEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }




    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DBContract.PATH_TIMETABLE, TIME_TABLE);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE + "/*", TIME_TABLE_DAY);
        matcher.addURI(authority, DBContract.PATH_ASSIGNMENT, ASSIGNMENTS);
        matcher.addURI(authority, DBContract.PATH_ATTENDANCE + "/*", ATTENDANCE);
        matcher.addURI(authority, DBContract.PATH_TEST, TESTS);
        matcher.addURI(authority, DBContract.PATH_COURSE, COURSES); //course table has attendance percentage with course
        matcher.addURI(authority, DBContract.PATH_TIMETABLE_WITH_ATTENDANCE+ "/*", TIME_TABLE_WITH_ATTENDANCE);

        matcher.addURI(authority, DBContract.PATH_TIMETABLE_MAX_DATE, TIMETABLE_MAX_DATE);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE_MIN_DATE, TIMETABLE_MIN_DATE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case TIME_TABLE_DAY: {
                retCursor = getTimeTable(uri);
                break;
            }
            case ASSIGNMENTS: {
                retCursor=getAssignments(uri);
                break;
            }

            case TIMETABLE_MAX_DATE:{
                retCursor = getTimeTableMaxDate();
                break;
            }
            case TIMETABLE_MIN_DATE:{
                retCursor = getTimeTableMinDate();
                break;
            }
            case COURSES: {
                retCursor = getAttendancePercentages(uri);
                break;
            }
            case TESTS: {
                retCursor = getTests(uri);
                break;
            }
            case TIME_TABLE_WITH_ATTENDANCE: {
                retCursor = getTimeTableWithAttendance(uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TESTS:
                return TestsEntry.CONTENT_ITEM_TYPE;
            case ASSIGNMENTS:
                return AssignmentsEntry.CONTENT_TYPE;
            case TIME_TABLE:
                return  TimeTableEntry.CONTENT_TYPE;
            case COURSES:
                return  CourseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    private Cursor getTimeTableMaxDate() {
        db=mOpenHelper.getReadableDatabase();

        //return db.query(TimeTableEntry.TABLE_NAME, new String [] {"MAX("+TimeTableEntry.COLUMN_DATE+")"}, null, null, null, null, null);
        return db.rawQuery("SELECT MAX("+TimeTableEntry.COLUMN_DATE+") FROM "+TimeTableEntry.TABLE_NAME, null);

    }

    private Cursor getTimeTableMinDate() {
        db=mOpenHelper.getReadableDatabase();

        //return db.query(TimeTableEntry.TABLE_NAME, new String [] {"MIN("+TimeTableEntry.COLUMN_DATE+")"}, null, null, null, null, null);

        return db.rawQuery("SELECT MIN("+TimeTableEntry.COLUMN_DATE+") FROM "+TimeTableEntry.TABLE_NAME, null);

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLiteConstraintException {

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db=mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch(match){
            case COURSES:{
                db.update(CourseEntry.TABLE_NAME,values,selection,selectionArgs);
            }
        }
        return 0;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        db=mOpenHelper.getWritableDatabase();
        int returnCount = 0;

        try{
            db.beginTransaction();

            final int match = sUriMatcher.match(uri);


            long _id;

            switch (match) {
                case ASSIGNMENTS:{
                    Timber.i("Attempting insert of %d Assignments", values.length);
                    for(ContentValues value:values){
                        _id=db.insertWithOnConflict(AssignmentsEntry.TABLE_NAME,null,value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    break;
                }
                case TIME_TABLE:{
                    Timber.i("Attempting insert of %d Schedule Rows", values.length);
                    for(ContentValues value:values){
                        if(value!=null){
                            Timber.i("value %s",value.getAsString(TimeTableEntry.COLUMN_COURSE));
                            _id=db.insertWithOnConflict(TimeTableEntry.TABLE_NAME,null,value, SQLiteDatabase.CONFLICT_REPLACE);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }

                    }
                    break;
                }
                case TESTS:{
                    Timber.i("Attempting insert of %d Test Rows", values.length);
                    for(ContentValues value:values){
                        _id=db.insertWithOnConflict(TestsEntry.TABLE_NAME,null,value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    break;
                }
                case COURSES:{
                    Timber.i("Attempting insert of %d Course Rows", values.length);
                    for(ContentValues value:values){
                        _id=db.insertWithOnConflict(CourseEntry.TABLE_NAME,null,value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    break;
                }
                default:{
                    return super.bulkInsert(uri, values);
                }

            }
            getContext().getContentResolver().notifyChange(uri, null);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            Log.i("DB","Ended transaction");
        }


        return returnCount;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
