package com.ir.ali.note

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.databasedao.NoteDAO
import com.ir.ali.note.database.notedatamodel.NotesDataModel
import com.ir.ali.note.databinding.ActivityNoteBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
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
        binding.icDeleteNote.setOnClickListener {
            if (
                binding.edtNoteTitle.text.isEmpty() && binding.edtNoteText.text.isEmpty()
            )
                finish()
            else {
                MaterialAlertDialogBuilder(this).apply {
                    setTitle("Move this note to trash ?")
                    setMessage(
                        "Note will be move to trash, you still can have access to note in trash"
                    )
                    setNegativeButton("Cancel") { _, _ -> }
                    setPositiveButton("Delete") { _, _ -> finish() }
                }.create().show()
            }
        }
    }

    private fun saveNote(noteTitle: String, noteText: String) {
        if (noteTitle.isNotEmpty() || noteText.isNotEmpty()) {
            val note = NotesDataModel(
                0,
                noteTitle,
                noteText,
                DataBaseHelper.STATE_FALSE,
                DataBaseHelper.STATE_FALSE,
                getDate()
            )
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.animate_activity_enter,
                R.anim.animate_activity_exit
            )
        } else {
            this@NoteActivity.overridePendingTransition(
                R.anim.animate_activity_enter, R.anim.animate_activity_exit
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}