package com.ir.ali.note.datamodel

data class NotesDataModel(
    var noteId: Int,
    var noteTitle: String,
    var noteText: String,
    var noteDeleteState: String,
    var noteArchiveState: String,
    var noteDate: String
)