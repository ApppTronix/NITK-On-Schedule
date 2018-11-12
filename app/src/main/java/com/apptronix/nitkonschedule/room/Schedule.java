package com.apptronix.nitkonschedule.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "schedule", primaryKeys = {"word"})
public class Schedule {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;

    @NonNull
    @ColumnInfo(name = "description")
    private String desc;

    @NonNull
    @ColumnInfo(name = "course")
    private String course;

    @NonNull
    @ColumnInfo(name = "date")
    private long date;

    @NonNull
    @ColumnInfo(name = "time")
    private long time;

    public Schedule(String word) {this.mWord = word;}

}