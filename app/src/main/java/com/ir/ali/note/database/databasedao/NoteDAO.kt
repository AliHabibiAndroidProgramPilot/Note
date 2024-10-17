package com.ir.ali.note.database.databasedao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.ir.ali.note.adapters.NoteDataModel
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.notedatamodel.NotesDataModel

class NoteDAO(private val dataBase: DataBaseHelper) {
    private val contentValues = ContentValues()
    private lateinit var cursor: Cursor

    fun insertNote(note: NotesDataModel): Boolean {
        val accessDataBase = dataBase.writableDatabase
        setContentValues(note)
        val insertResult =
            accessDataBase.insert(DataBaseHelper.NOTES_TABLE, "null", contentValues)
        accessDataBase.close()
        return insertResult > 0
    }

    fun getNotes(deleted: String, archived: String): ArrayList<NoteDataModel> {
        val accessDataBase = dataBase.readableDatabase
        val sqlQuery: String =
            "SELECT ${DataBaseHelper.NOTES_ID}, ${DataBaseHelper.NOTES_TITLE}, " +
                    "${DataBaseHelper.NOTES_TEXT}, ${DataBaseHelper.NOTES_DATE} " +
                    "FROM ${DataBaseHelper.NOTES_TABLE} " +
                    "WHERE ${DataBaseHelper.NOTES_DELETE_STATE} = ? " +
                    "OR ${DataBaseHelper.NOTES_ARCHIVE_STATE} = ?"
        cursor = accessDataBase.rawQuery(
            sqlQuery,
            arrayOf(deleted, archived)
        )
        val data = getDataFromCursor()
        cursor.close()
        accessDataBase.close()
        return data
    }

    private fun getDataFromCursor(): ArrayList<NoteDataModel> {
        val tempData = ArrayList<NoteDataModel>()
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_ID))
                    val title =
                        cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_TITLE))
                    val text =
                        cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_TEXT))
                    val date =
                        cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_DATE))
                    tempData.add(
                        NoteDataModel(id, title, text, date)
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: IllegalArgumentException) {
            Log.e("IllegalArgumentException", e.cause.toString())
        }
        return tempData
    }

    private fun setContentValues(note: NotesDataModel) {
        contentValues.clear()
        contentValues.put(DataBaseHelper.NOTES_TITLE, note.noteTitle)
        contentValues.put(DataBaseHelper.NOTES_TEXT, note.noteText)
        contentValues.put(DataBaseHelper.NOTES_DELETE_STATE, note.noteDeleteState)
        contentValues.put(DataBaseHelper.NOTES_ARCHIVE_STATE, note.noteArchiveState)
        contentValues.put(DataBaseHelper.NOTES_DATE, note.noteData)
    }
}