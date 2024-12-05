package com.ir.ali.note.database

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.ir.ali.note.datamodel.NoteDataModelForRecycler
import com.ir.ali.note.datamodel.NotesDataModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    fun getNotes(deleted: String, archived: String): ArrayList<NoteDataModelForRecycler> {
        val accessDataBase = dataBase.readableDatabase
        val sqlQuery: String =
            "SELECT ${DataBaseHelper.NOTES_ID}, ${DataBaseHelper.NOTES_TITLE}, " +
                    "${DataBaseHelper.NOTES_TEXT}, ${DataBaseHelper.NOTES_DATE} " +
                    "FROM ${DataBaseHelper.NOTES_TABLE} " +
                    "WHERE ${DataBaseHelper.NOTES_DELETE_STATE} = ? " +
                    "AND ${DataBaseHelper.NOTES_ARCHIVE_STATE} = ? " +
                    "ORDER BY ${DataBaseHelper.NOTES_ID} DESC"
        cursor = accessDataBase.rawQuery(
            sqlQuery,
            arrayOf(deleted, archived)
        )
        val data = getDataFromCursor()
        cursor.close()
        accessDataBase.close()
        return data
    }

    fun getNoteById(noteId: Int): NotesDataModel {
        val accessDataBase = dataBase.readableDatabase
        val sqlQuery =
            "SELECT * FROM ${DataBaseHelper.NOTES_TABLE} WHERE ${DataBaseHelper.NOTES_ID} = ?"
        cursor = accessDataBase.rawQuery(sqlQuery, arrayOf(noteId.toString()))
        val data = getDataFromCursorById()
        cursor.close()
        accessDataBase.close()
        return data
    }

    fun updateNoteById(noteId: Int, note: NotesDataModel): Boolean {
        val accessDataBase = dataBase.writableDatabase
        setContentValues(note)
        val updateResult =
            accessDataBase.update(
                DataBaseHelper.NOTES_TABLE,
                contentValues,
                "${DataBaseHelper.NOTES_ID} = ?",
                arrayOf(noteId.toString())
            )
        accessDataBase.close()
        return updateResult > 0
    }

    fun updateNoteDeleteState(noteId: Int, noteDeleteState: String): Boolean {
        val accessDataBase = dataBase.writableDatabase
        contentValues.clear()
        contentValues.put(DataBaseHelper.NOTES_DELETE_STATE, noteDeleteState)
        val updateResult =
            accessDataBase.update(
                DataBaseHelper.NOTES_TABLE,
                contentValues,
                "${DataBaseHelper.NOTES_ID} = ?",
                arrayOf(noteId.toString())
            )
        accessDataBase.close()
        return updateResult > 0
    }

    fun updateNoteArchiveState(noteId: Int, noteArchiveState: String): Boolean {
        val accessDataBase = dataBase.writableDatabase
        contentValues.clear()
        contentValues.put(DataBaseHelper.NOTES_ARCHIVE_STATE, noteArchiveState)
        val updateResult =
            accessDataBase.update(
                DataBaseHelper.NOTES_TABLE,
                contentValues,
                "${DataBaseHelper.NOTES_ID} = ?",
                arrayOf(noteId.toString())
            )
        accessDataBase.close()
        return updateResult > 0
    }

    fun deleteAllTrashNotes(): Boolean {
        val accessDataBase = dataBase.writableDatabase
        val deleteResult =
            accessDataBase.delete(
                DataBaseHelper.NOTES_TABLE,
                "${DataBaseHelper.NOTES_DELETE_STATE} = ?",
                arrayOf(DataBaseHelper.STATE_TRUE)
            )
        accessDataBase.close()
        return deleteResult > 0
    }

    private fun getDataFromCursor(): ArrayList<NoteDataModelForRecycler> {
        val cursorData = ArrayList<NoteDataModelForRecycler>()
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
                        checkDate(
                            cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_DATE)),
                            getDate()
                        )
                    cursorData.add(
                        NoteDataModelForRecycler(id, title, text, date)
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: IllegalArgumentException) {
            Log.e("IllegalArgumentException", e.cause.toString())
        }
        return cursorData
    }

    private fun getDataFromCursorById(): NotesDataModel {
        val tempNoteData =
            NotesDataModel(0, "", "", "", "", "")
        try {
            if (cursor.moveToFirst()) {
                tempNoteData.noteId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_ID))
                tempNoteData.noteTitle =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_TITLE))
                tempNoteData.noteText =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_TEXT))
                tempNoteData.noteDeleteState =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_DELETE_STATE))
                tempNoteData.noteArchiveState =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_ARCHIVE_STATE))
                tempNoteData.noteDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NOTES_DATE))
            }
        } catch (e: IllegalArgumentException) {
            Log.e("IllegalArgumentException", e.cause.toString())
        }
        return tempNoteData
    }

    private fun setContentValues(note: NotesDataModel) {
        contentValues.clear()
        contentValues.put(DataBaseHelper.NOTES_TITLE, note.noteTitle)
        contentValues.put(DataBaseHelper.NOTES_TEXT, note.noteText)
        contentValues.put(DataBaseHelper.NOTES_DELETE_STATE, note.noteDeleteState)
        contentValues.put(DataBaseHelper.NOTES_ARCHIVE_STATE, note.noteArchiveState)
        contentValues.put(DataBaseHelper.NOTES_DATE, note.noteDate)
    }

    private fun splitDate(date: String): HashMap<String, String> {
        val tempDateParts = date.split("[, ]+".toRegex())
        return hashMapOf(
            "dayOfWeek" to tempDateParts[0],
            "monthOfYear" to tempDateParts[1],
            "year" to tempDateParts[2]
        )
    }

    private fun checkDate(noteDate: String, currentDate: String): String {
        val splitNoteDate = splitDate(noteDate)
        val splitCurrentDate = splitDate(currentDate)
        return if (
            splitNoteDate["dayOfWeek"] == splitCurrentDate["dayOfWeek"] &&
            splitNoteDate["monthOfYear"] == splitCurrentDate["monthOfYear"] &&
            splitNoteDate["year"] == splitCurrentDate["year"]
        ) {
            // Returns The Today If Note Date Is Today
            "Today"
        } else if (splitNoteDate["year"] == splitCurrentDate["year"]) {
            // Returns Just Month And Date If Note Year Is Same With Current Year
            splitNoteDate["dayOfWeek"] + " " + splitNoteDate["monthOfYear"]
        } else
            noteDate
    }

    private fun getDate(): String {
        val currentDate = LocalDate.now()
        // pattern: 28 May, 2024
        val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
        return currentDate.format(formatter)
    }
}