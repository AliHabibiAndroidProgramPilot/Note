package com.ir.ali.note.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ir.ali.note.NoteActivity
import com.ir.ali.note.R
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.datamodel.NoteDataModelForRecycler
import com.ir.ali.note.databinding.NoteListItemBinding

class NoteRecyclerAdapter(
    private val contextActivity: Activity,
    private var notes: ArrayList<NoteDataModelForRecycler>,
    private val databaseDao: NoteDAO
) : RecyclerView.Adapter<NoteRecyclerAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomViewHolder(
            NoteListItemBinding.inflate(
                contextActivity.layoutInflater, parent, false
            )
        )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.setData(notes[position])
    }

    override fun getItemCount() = notes.size

    @SuppressLint("NotifyDataSetChanged")
    fun notifyRecycler(changedData: ArrayList<NoteDataModelForRecycler>) {
        //  Relying On notifyDataSetChanged As A Last Resort
        notes = changedData
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(
        private val binding: NoteListItemBinding
    ) : ViewHolder(binding.root) {
        fun setData(noteDetails: NoteDataModelForRecycler) {
            binding.txtNoteTitle.visibility = if (noteDetails.noteTitle.isEmpty()) View.GONE
            else
                View.VISIBLE
            binding.txtNoteText.visibility = if (noteDetails.noteText.isEmpty()) View.GONE
            else
                View.VISIBLE
            binding.txtNoteTitle.text = noteDetails.noteTitle
            binding.txtNoteText.text = noteDetails.noteText
            binding.txtNoteDate.text = noteDetails.noteDate
            // Manage Delete Icon Functionality
            binding.icDeleteNote.setOnClickListener {
                MaterialAlertDialogBuilder(contextActivity).apply {
                    setTitle("Move this note to trash ?")
                    setMessage(
                        "Note will be move to trash, you still can have access to note in trash"
                    )
                    setPositiveButton("Delete") { _, _ ->
                        val updateResult =
                            databaseDao.updateNoteDeleteState(noteDetails.noteId, DataBaseHelper.STATE_TRUE)
                        notes.removeAt(layoutPosition)
                        notifyItemRemoved(layoutPosition)
                    }
                    setNegativeButton("Cancel") { _, _ -> }
                }.create().show()
            }
            // Intent to Note Activity To See And Edite Notes
            binding.secondRoot.setOnClickListener {
                contextActivity.startActivity(
                    Intent(contextActivity, NoteActivity::class.java)
                        .putExtra("IS_NEW_NOTE", false)
                        .putExtra(DataBaseHelper.NOTES_ID, noteDetails.noteId)
                )
                //region Intent With Animation
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    contextActivity.overrideActivityTransition(
                        AppCompatActivity.OVERRIDE_TRANSITION_OPEN,
                        R.anim.animate_activity_enter,
                        R.anim.animate_activity_exit
                    )
                } else {
                    contextActivity.overridePendingTransition(
                        R.anim.animate_activity_enter, R.anim.animate_activity_exit
                    )
                }
                //endregion
            }
        }
    }
}