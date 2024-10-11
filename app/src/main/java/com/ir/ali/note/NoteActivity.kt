package com.ir.ali.note

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.databasedao.NoteDAO
import com.ir.ali.note.database.notedatamodel.NotesDataModel
import com.ir.ali.note.databinding.ActivityNoteBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private val accessDataBase = NoteDAO(DataBaseHelper(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.edtNoteText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun saveNote(noteTitle: String, noteText: String) {
        if (noteTitle.isNotEmpty() || noteText.isNotEmpty()) {
            val note = NotesDataModel(0, noteTitle, noteText, DataBaseHelper.DELETE_STATE_FALSE, getDate())
           accessDataBase.insertNote(note)
        } else if(noteTitle.isEmpty() && noteText.isEmpty()) {
            val sharedPreferences = getSharedPreferences("SNACK_BAR", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("SHOW_SNACK_BAR", true)
            editor.apply()
        }
    }

    private fun getDate(): String {
        val currentDate = LocalDate.now()
        // pattern: 28 May, 2024
        val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
        return currentDate.format(formatter)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "super.onBackPressed()",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )

    override fun onBackPressed() {
        saveNote(binding.edtNoteTitle.text.toString(), binding.edtNoteText.text.toString())
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}