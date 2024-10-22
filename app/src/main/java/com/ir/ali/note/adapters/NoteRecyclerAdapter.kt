package com.ir.ali.note.adapters

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    fun changedNoteData(newData: ArrayList<NoteDataModelForRecycler>) {
        if (newData.size > notes.size) {
            notes = newData
            notifyItemInserted(notes.size)
        }
    }

    inner class CustomViewHolder(
        private val binding: NoteListItemBinding
    ) : ViewHolder(binding.root) {
        fun setData(noteDetails: NoteDataModelForRecycler) {
            binding.txtNoteTitle.text = noteDetails.noteTitle
            binding.txtNoteText.text = noteDetails.noteText
            binding.txtNoteDate.text = noteDetails.noteDate

            //Manage Delete Icon Functionality
            binding.icDeleteNote.setOnClickListener {
                MaterialAlertDialogBuilder(contextActivity).apply {
                    setTitle("Move this note to trash ?")
                    setMessage(
                        "Note will be move to trash, you still can have access to note in trash"
                    )
                    setPositiveButton("Delete") { _, _ ->
                        val updateResult =
                            databaseDao.updateNote(noteDetails.noteId, DataBaseHelper.STATE_TRUE)
                        notes.removeAt(layoutPosition)
                        notifyItemRemoved(layoutPosition)
                    }
                    setNegativeButton("Cancel") { _, _ -> }
                }.create().show()
            }
        }
    }
}