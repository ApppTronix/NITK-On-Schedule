package com.apptronix.nitkonschedule.student.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DevOpsTrends on 12/7/2016.
 */

public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.apptronix.nitkonschedule.student";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TIMETABLE = "timetable";

    public static final String PATH_ASSIGNMENT = "assignment";

    public static final String PATH_TEST = "test";

    public static final String PATH_ATTENDANCE = "attendance";


    public static final String PATH_COURSE = "course";

    public static final String PATH_REPOSITORY = "repository";



    public static final String PATH_TIMETABLE_WITH_ATTENDANCE = "timetable_attendace";


    public static final String PATH_ATTENDANCE_HIST = "attendance_hist";


    public static final String PATH_TIMETABLE_MAX_DATE = "timetable_max_date";

    public static final String PATH_TIMETABLE_MIN_DATE = "timetable_min_date";


    public static  final class TimeTableEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE).build();


        public static final Uri ATTENDANCE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ATTENDANCE_HIST).build();
        public static final Uri WITH_ATTENDANCE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE_WITH_ATTENDANCE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMETABLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMETABLE;

        public static final String TABLE_NAME = "timetable";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_SHORT_NAME = "shortname";

        public static final String COLUMN_PRESENT = "present";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_TIME = "time";

        public static final String COLUMN_COURSE = "course";

        public static Uri buildDateTableUri(int date){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(date)).build();
        }


        public static Uri maxDate(){
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE_MAX_DATE).build();
        }

        public static Uri minDate(){
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE_MIN_DATE).build();
        }

        public static Uri buildDayTableWithAttendanceUri(double date){
            return WITH_ATTENDANCE_URI.buildUpon().appendPath(String.valueOf(date)).build();
        }

        public static String getTimeTableDay(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    public static  final class AssignmentsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ASSIGNMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSIGNMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSIGNMENT;

        public static final String TABLE_NAME = "assignment";

        public static final String COLUMN_SUBMISSION_DATE = "submission_date";

        public static final String COLUMN_COURSE_CODE = "course_code";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_GRADE = "grade";

        public static final String COLUMN_WEIGHTAGE = "weightage";

    }


    public static  final class CourseEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        public static final String TABLE_NAME = "course";

        public static final String COLUMN_COURSE = "course_code";

        public static final String COLUMN_COURSE_ENROLLED = "course_enrolled";

        public static final String COLUMN_SEMESTER = "course_semester";

        public static final String COLUMN_GRADES = "course_grades";

        public static final String COLUMN_ATT_PERCENT = "attendance_percentage";

        public static final String COLUMN_COURSE_DESCRIPTION = "course_description";

        public static Uri buildCourseUri(String course){
            return CONTENT_URI.buildUpon().appendPath(course).build();
        }

        public static String getCourse(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    public static  final class RepositoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPOSITORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        public static final String TABLE_NAME = "respository";

        public static final String COLUMN_COURSE = "course_code";

        public static final String COLUMN_FILE_NAME = "file_name";

        public static final String COLUMN_FILE_LOCATION = "file_location";

        public static Uri buildRepositoryUri(String course){
            return CONTENT_URI.buildUpon().appendPath(course).build();
        }

        public static String getCourse(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    public static  final class TestsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSIGNMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSIGNMENT;

        public static final String TABLE_NAME = "test";

        public static final String COLUMN_TEST_DATE = "submission_date";

        public static final String COLUMN_COURSE_CODE = "course_code";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_SYLLABUS = "syllabus";

        public static final String COLUMN_GRADE = "grade";

        public static final String COLUMN_WEIGHTAGE = "weightage";

        public static Uri buildTestUri(int movieId){

            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }
    }

}
