package com.ir.ali.note

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.ActivityNoteBinding
import com.ir.ali.note.datamodel.NotesDataModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private val databaseDao = NoteDAO(DataBaseHelper(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //region Set ToolBar as Action Bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //endregion
        if (intent.getBooleanExtra("IS_NEW_NOTE", false)) {
            binding.noteDate.text = getDate()
            //region request Focus
            binding.edtNoteText.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            //endregion
        } else {
            val noteId = intent.getIntExtra(DataBaseHelper.NOTES_ID, 0)
            val note: NotesDataModel = databaseDao.getNoteById(noteId)
            val editable = Editable.Factory()
            binding.edtNoteTitle.text = editable.newEditable(note.noteTitle)
            binding.edtNoteText.text = editable.newEditable(note.noteText)
            binding.noteDate.text = editable.newEditable(note.noteDate)
        }
        binding.icDeleteNote.setOnClickListener {
            if (
                binding.edtNoteTitle.text.isEmpty() && binding.edtNoteText.text.isEmpty()
            )
                finish()
            else {
                //region Create Alert Dialog
                MaterialAlertDialogBuilder(this).apply {
                    setTitle("Move this note to trash ?")
                    setMessage(
                        "Note will be move to trash, you still can have access to note in trash"
                    )
                    setNegativeButton("Cancel") { _, _ -> TODO() }
                    setPositiveButton("Delete") { _, _ ->  TODO() }
                }.create().show()
                //endregion
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
           databaseDao.insertNote(note)
        } else if(noteTitle.isEmpty() && noteText.isEmpty()) {
            val sharedPreferences = getSharedPreferences("SNACK_BAR", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("SHOW_SNACK_BAR", true)
            editor.apply()
        }
    }

    private fun changeNote() {

    }

    private fun getDate(): String {
        val currentDate = LocalDate.now()
        // pattern: 12 January
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
        if (intent.getBooleanExtra("IS_NEW_NOTE", false)) {
            // Start Save Note Immediately After User Wants to Get Back to Main Page
            saveNote(binding.edtNoteTitle.text.toString(), binding.edtNoteText.text.toString())
        } else {
            changeNote()
        }
        super.onBackPressed()
        //region Back to Main Page With Animation
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
        //endregion
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}