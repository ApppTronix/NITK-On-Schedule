package com.apptronix.nitkonschedule.teacher.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.Log;

import com.apptronix.nitkonschedule.teacher.data.DBContract.AssignmentsEntry;
import com.apptronix.nitkonschedule.teacher.data.DBContract.CourseEntry;
import com.apptronix.nitkonschedule.teacher.data.DBContract.TestsEntry;
import com.apptronix.nitkonschedule.teacher.data.DBContract.TimeTableEntry;
import com.apptronix.nitkonschedule.teacher.data.DBContract.RepositoryEntry;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 12/8/2016.
 */

public class DBProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    static final int TIME_TABLE = 100;
    static final int ASSIGNMENTS = 101;
    static final int ASSIGNMENTS_COURSE = 114;
    static final int TESTS = 102;
    static final int TESTS_COURSE = 115;
    static final int ATTENDANCE = 103;
    static final int TIME_TABLE_DAY = 104;
    static final int ASSIGNMENT = 108;
    static final int TEST = 109;
    static final int TIMETABLE_MAX_DATE = 110;
    static final int TIMETABLE_MIN_DATE = 111;
    static final int COURSES = 112;
    static final int TIME_TABLE_ATTD = 113;
    static final int TIME_TABLE_ID = 116;
    static final int COURSE = 117;
    static final int REPOSITORY = 107;

    private static final SQLiteQueryBuilder sStudentQueryBuilder;

    private SQLiteDatabase db;

    static{

        sStudentQueryBuilder = new SQLiteQueryBuilder();

        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(TestsEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(AssignmentsEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(CourseEntry.TABLE_NAME);
        sStudentQueryBuilder.setTables(DBContract.RepositoryEntry.TABLE_NAME);

    }

    private Cursor getTimeTable(Uri uri) {

        String day = TimeTableEntry.getDate(uri);
        Timber.i("schedule provider %s", day);

        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                TimeTableEntry.TABLE_NAME +"."+ TimeTableEntry.COLUMN_DATE + " = ? ",
                new String[]{day},
                null,
                null,
                TimeTableEntry.COLUMN_TIME + " ASC"
        );

    }

    private Cursor getTimeTableFromID(Uri uri) {

        String id = TimeTableEntry.getScheduleId(uri);
        Timber.i("provider %s", id);

        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                TimeTableEntry.TABLE_NAME +"."+ TimeTableEntry._ID + " = ? ",
                new String[]{id},
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

    private Cursor getAttendance(Uri uri) {
        db=mOpenHelper.getReadableDatabase();

        Timber.i("attendance queried");
        String date=TimeTableEntry.getDate(uri);
        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                TimeTableEntry.TABLE_NAME + "." + TimeTableEntry.COLUMN_DATE + " <= ? ",
                new String[]{date},
                null,
                null,
                null
        );

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

    private Cursor getAssignment(Uri uri) {
        String id = AssignmentsEntry.getAssignmentId(uri);

        db=mOpenHelper.getReadableDatabase();

        sStudentQueryBuilder.setTables(AssignmentsEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                AssignmentsEntry.TABLE_NAME+"."+ AssignmentsEntry._ID + " = ? ",
                new String[]{id},
                null,
                null,
                null
        );
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DBContract.PATH_TIMETABLE_ATTD+  "/#", TIME_TABLE_ATTD);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE_ATTD, TIME_TABLE_ATTD);
        matcher.addURI(authority, DBContract.PATH_COURSE+  "/*", COURSE);
        matcher.addURI(authority, DBContract.PATH_COURSE, COURSES);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE_MAX_DATE, TIMETABLE_MAX_DATE);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE_MIN_DATE, TIMETABLE_MIN_DATE);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE + "/*", TIME_TABLE_DAY);
        matcher.addURI(authority, DBContract.PATH_ASSIGNMENT, ASSIGNMENTS);
        matcher.addURI(authority, DBContract.PATH_ATTENDANCE+"/#", ATTENDANCE);
        matcher.addURI(authority, DBContract.PATH_TEST, TESTS);
        matcher.addURI(authority, DBContract.PATH_TIMETABLE_ID+  "/#", TIME_TABLE_ID);
        matcher.addURI(authority, DBContract.PATH_ASSIGNMENT+  "/#", ASSIGNMENT);
        matcher.addURI(authority, DBContract.PATH_TEST+  "/#", TEST);
        matcher.addURI(authority, DBContract.PATH_TEST+  "/code/*", 2222);
        matcher.addURI(authority, DBContract.PATH_REPOSITORY+  "/*", REPOSITORY);
        matcher.addURI(authority, DBContract.PATH_REPOSITORY, REPOSITORY);
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
        Timber.i("s %d",sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case TIME_TABLE_ID:{
                retCursor = getTimeTableFromID(uri);
                break;
            }
            case REPOSITORY:{
                retCursor = getRepository(uri);
                break;
            }
            case TIME_TABLE_DAY: {
                retCursor = getTimeTable(uri);
                Timber.i("time table day %d",retCursor.getCount());
                break;
            }
            case ATTENDANCE: {
                retCursor = getAttendance(uri);
                break;
            }
            case COURSES:{
                retCursor=getCourses();
                break;
            }
            case COURSE:{
                retCursor=getCourse(uri);
                break;
            }
            case TIMETABLE_MAX_DATE:{
                retCursor = getTimeTableMaxDate(uri);
                break;
            }
            case TIMETABLE_MIN_DATE:{
                retCursor = getTimeTableMinDate(uri);
                break;
            }
            case ASSIGNMENTS: {
                retCursor=getAssignments(uri);
                break;
            }
            case TESTS: {
                retCursor = getTests(uri);
                break;
            }
            case ASSIGNMENT: {
                retCursor = getAssignment(uri);
                break;
            }
            case TEST: {
                retCursor = getTest(uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    private Cursor getRepository(Uri uri) {
        db=mOpenHelper.getReadableDatabase();
        sStudentQueryBuilder.setTables(DBContract.RepositoryEntry.TABLE_NAME);

        return sStudentQueryBuilder.query(db,
                null,
                DBContract.RepositoryEntry.TABLE_NAME + "." + DBContract.RepositoryEntry.COLUMN_COURSE + " <= ? ",
                new String[]{DBContract.RepositoryEntry.getCourse(uri)},
                null,
                null,
                null
        );

    }

    private Cursor getCourse(Uri uri) {

        db=mOpenHelper.getReadableDatabase();
        sStudentQueryBuilder.setTables(CourseEntry.TABLE_NAME);

        return sStudentQueryBuilder.query(db,
                null,
                CourseEntry.TABLE_NAME + "." + CourseEntry.COLUMN_COURSE + " = ? ",
                new String[]{CourseEntry.getCourse(uri)},
                null,
                null,
                null
        );
    }

    private Cursor getTest(Uri uri) {
        db=mOpenHelper.getReadableDatabase();

        String id = TestsEntry.getTestId(uri);
        sStudentQueryBuilder.setTables(TestsEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                TimeTableEntry.TABLE_NAME +"."+ TimeTableEntry._ID + " = ? ",
                new String[]{id},
                null,
                null,
                null
        );
    }

    private Cursor getTimeTableAttd(Uri uri) {

        db=mOpenHelper.getReadableDatabase();


        sStudentQueryBuilder.setTables(TimeTableEntry.TABLE_NAME);
        return sStudentQueryBuilder.query(db,
                null,
                TimeTableEntry.TABLE_NAME + "." + TimeTableEntry.COLUMN_DATE + " = ? ",
                new String[]{TimeTableEntry.getDate(uri)},
                null,
                null,
                null
        );
    }

    private Cursor getCourses() {
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

    private Cursor getTimeTableMaxDate(Uri uri) {
        db=mOpenHelper.getReadableDatabase();

        //return db.query(TimeTableEntry.TABLE_NAME, new String [] {"MAX("+TimeTableEntry.COLUMN_DATE+")"}, null, null, null, null, null);
        return db.rawQuery("SELECT MAX("+TimeTableEntry.COLUMN_DATE+") FROM "+TimeTableEntry.TABLE_NAME, null);

    }

    private Cursor getTimeTableMinDate(Uri uri) {
        db=mOpenHelper.getReadableDatabase();

        //return db.query(TimeTableEntry.TABLE_NAME, new String [] {"MIN("+TimeTableEntry.COLUMN_DATE+")"}, null, null, null, null, null);

        return db.rawQuery("SELECT MIN("+TimeTableEntry.COLUMN_DATE+") FROM "+TimeTableEntry.TABLE_NAME, null);

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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLiteConstraintException {
        db=mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES: {
                db.insert(CourseEntry.TABLE_NAME, null, values);
            }
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        db=mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ASSIGNMENT:{
                return db.delete(AssignmentsEntry.TABLE_NAME,"_id = ?",new String[]{AssignmentsEntry.getAssignmentId(uri)});
            }
            case TEST:{
                return db.delete(TestsEntry.TABLE_NAME,"_id = ?",new String[]{TestsEntry.getTestId(uri)});
            }
            case  TIME_TABLE_ID:{
                return db.delete(TimeTableEntry.TABLE_NAME,"_id = ?",new String[]{TimeTableEntry.getScheduleId(uri)});

            }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db=mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REPOSITORY:{
                String course  = (String) values.get(RepositoryEntry.COLUMN_COURSE);
                String fileName  = (String) values.get(RepositoryEntry.COLUMN_FILE_NAME);
                values.get(RepositoryEntry.COLUMN_COURSE);
                return db.update(RepositoryEntry.TABLE_NAME,values,RepositoryEntry.COLUMN_FILE_NAME + " = ? AND "
                        + RepositoryEntry.COLUMN_COURSE + " = ? ",new String[]{fileName,course});
            }
            default:{
                return 0;
            }
        }

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
                case TIME_TABLE_ATTD:{
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
                case REPOSITORY:{
                    Timber.i("Attempting insert of %d Rep Rows", values.length);
                    for(ContentValues value:values){
                        _id=db.insertWithOnConflict(DBContract.RepositoryEntry.TABLE_NAME,null,value, SQLiteDatabase.CONFLICT_IGNORE);
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
