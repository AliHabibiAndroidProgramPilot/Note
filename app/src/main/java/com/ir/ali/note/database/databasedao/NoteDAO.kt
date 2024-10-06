package com.ir.ali.note.database.databasedao

import android.content.ContentValues
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.notedatamodel.NotesDataModel

class NoteDAO(
    private val dataBase: DataBaseHelper
) {
    private val contentValues = ContentValues()

    fun insertNote(note: NotesDataModel): Boolean {
        val accessDataBase = dataBase.writableDatabase
        setContentValues(note)
        val insertResult =
            accessDataBase.insert(DataBaseHelper.NOTES_TABLE, "null", contentValues)
        accessDataBase.close()
        return insertResult > 0
    }
    private fun setContentValues(note: NotesDataModel) {
        contentValues.clear()
        contentValues.put(DataBaseHelper.NOTES_TITLE, note.noteTitle)
        contentValues.put(DataBaseHelper.NOTES_TEXT, note.noteText)
        contentValues.put(DataBaseHelper.NOTES_DELETE_STATE, note.noteDeleteState)
        contentValues.put(DataBaseHelper.NOTES_DATA, note.noteData)
    }
}