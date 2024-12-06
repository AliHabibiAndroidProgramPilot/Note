package com.ir.ali.note

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ir.ali.note.adapters.NoteRecyclerAdapter
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.ActivityNoteBinding
import com.ir.ali.note.datamodel.NotesDataModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private lateinit var recyclerAdapter: NoteRecyclerAdapter
    private val databaseDao = NoteDAO(DataBaseHelper(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //region Set ToolBar as Action Bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //endregion
        if (intent.getBooleanExtra("IS_TRASH_NOTE", false)) {
            val edtTitle = binding.edtNoteTitle
            val edtText = binding.edtNoteText
            edtTitle.isFocusable = false
            edtTitle.isFocusableInTouchMode = false
            edtTitle.isCursorVisible = false
            edtText.isFocusable = false
            edtText.isFocusableInTouchMode = false
            edtText.isCursorVisible = false
            // Used Clickable Flag To Prevent Spam SnackBar By Multiply Clicking
            var isClickable = true
            val showSnackBar = View.OnClickListener {
                if (isClickable) {
                    Snackbar.make(
                        binding.root,
                        "Can't Edit Trashed Notes",
                        Snackbar.LENGTH_SHORT
                    ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
                    isClickable = false
                    it.postDelayed({
                        isClickable = true
                    }, 3500)
                }
            }
            edtTitle.setOnClickListener(showSnackBar)
            edtText.setOnClickListener(showSnackBar)
            binding.icArchiveNote.visibility = View.GONE
            binding.icPinNote.visibility = View.GONE
        }
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
                    setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    setPositiveButton("Delete") { _, _ ->
                        if (intent.getBooleanExtra("IS_NEW_NOTE", false))
                            finish()
                        else {
                            recyclerAdapter =
                                NoteRecyclerAdapter(this@NoteActivity, databaseDao)
                            val noteId = intent.getIntExtra(DataBaseHelper.NOTES_ID, 0)
                            databaseDao.updateNoteDeleteState(
                                noteId, DataBaseHelper.STATE_TRUE
                            )
                            recyclerAdapter.notifyRecycler(
                                databaseDao.getNotes(
                                    DataBaseHelper.STATE_FALSE,
                                    DataBaseHelper.STATE_FALSE
                                )
                            )
                            finish()
                        }
                    }
                }.create().show()
                //endregion
            }
        }
    }

    private fun saveNote(noteTitle: String, noteText: String) {
        val note = NotesDataModel(
            0,
            noteTitle,
            noteText,
            DataBaseHelper.STATE_FALSE,
            DataBaseHelper.STATE_FALSE,
            getDate()
        )
        databaseDao.insertNote(note)
    }

    private fun changeNote(noteId: Int, noteTitle: String, noteText: String) {
        val note = NotesDataModel(
            noteId,
            noteTitle,
            noteText,
            DataBaseHelper.STATE_FALSE,
            DataBaseHelper.STATE_FALSE,
            getDate()
        )
        databaseDao.updateNoteById(noteId, note)
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
            val noteTitle = binding.edtNoteTitle.text.toString()
            val noteText = binding.edtNoteText.text.toString()
            if (noteTitle.isNotEmpty() || noteText.isNotEmpty())
                saveNote(noteTitle, noteText)
            else if (noteTitle.isEmpty() && noteText.isEmpty()) {
                val sharedPreferences = getSharedPreferences("SNACK_BAR", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("SHOW_SNACK_BAR", true)
                editor.apply()
            }
        } else {
            val noteTitle: String = binding.edtNoteTitle.text.toString()
            val noteText: String = binding.edtNoteText.text.toString()
            val receivedNote: NotesDataModel =
                databaseDao.getNoteById(intent.getIntExtra(DataBaseHelper.NOTES_ID, 0))
            if (receivedNote.noteTitle != noteTitle || receivedNote.noteText != noteText)
                changeNote(receivedNote.noteId, noteTitle, noteText)
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