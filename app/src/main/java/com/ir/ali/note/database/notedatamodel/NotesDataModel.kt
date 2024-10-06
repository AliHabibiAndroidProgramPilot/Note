package com.ir.ali.note.database.notedatamodel

data class NotesDataModel(
    var noteId: Int,
    var noteTitle: String,
    var noteText: String,
    var noteDeleteState: String,
    var noteData: String
)