package com.ir.ali.note

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.databasedao.NoteDAO
import com.ir.ali.note.database.notedatamodel.NotesDataModel
import com.ir.ali.note.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private val accessDataBase = NoteDAO(DataBaseHelper(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.edtNoteTitle.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun saveNote(noteTitle: String, noteText: String) {
        if (noteTitle.isNotEmpty() || noteText.isNotEmpty()) {
            val note = NotesDataModel(0, noteTitle, noteText, "0", getData())
            val saveNoteResult = accessDataBase.insertNote(note)
            if(!saveNoteResult) {
                Snackbar.make(
                    binding.root, "Couldn't save note", Snackbar.LENGTH_SHORT
                ).show()

                TODO("Managing to Stop Closing Add Note Activity When Note didn't Saved" +
                        "saveNote function can return boolean via insertNote boolean")
            }
        } else if(noteTitle.isEmpty() && noteText.isEmpty()) {
            Snackbar.make(
                binding.root, "Empty note discarded", Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun getData() : String {
        return "test"
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "super.onBackPressed()",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )

    override fun onBackPressed() {
        saveNote(binding.edtNoteTitle.toString(), binding.edtNoteText.toString())
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        saveNote(binding.edtNoteTitle.toString(), binding.edtNoteText.toString())
        onBackPressed()
        return true
    }
}