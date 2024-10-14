package com.ir.ali.note.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context, DB_NAME, null, DB_VERSION
) {
    companion object{
        //Data Base Name And Version
        private const val DB_NAME = "note.db"
        private const val DB_VERSION = 1

        //Note Table
        const val NOTES_TABLE = "Note"
        const val NOTES_ID = "Id"
        const val NOTES_TITLE = "Title"
        const val NOTES_TEXT = "Text"
        const val NOTES_DELETE_STATE = "Delete_State"
        const val NOTES_ARCHIVE_STATE = "Archive_State"
        const val NOTES_DATE = "Date"

        //Delete State Variables
        const val DELETE_STATE_FALSE = "0"
        const val DELETE_STATE_TRUE = "1"

        //Archive State Variables
        const val ARCHIVE_STATE_FALSE = "0"
        const val ARCHIVE_STATE_TRUE = "1"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $NOTES_TABLE (" +
                    "$NOTES_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$NOTES_TITLE VARCHAR(255)," +
                    "$NOTES_TEXT TEXT," +
                    "$NOTES_DELETE_STATE VARCHAR(1)," +
                    "$NOTES_ARCHIVE_STATE VARCHAR(1)," +
                    "$NOTES_DATE VARCHAR(150))"
        )
    }
    //No Need For Upgrade DataBase At This Time.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}