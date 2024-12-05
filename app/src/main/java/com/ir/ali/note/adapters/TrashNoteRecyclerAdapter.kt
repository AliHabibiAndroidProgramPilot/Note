package com.ir.ali.note.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ir.ali.note.NoteActivity
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.NoteTrashItemBinding
import com.ir.ali.note.datamodel.NoteDataModelForRecycler

class TrashNoteRecyclerAdapter(
    private val context: Context,
    private val databaseDAO: NoteDAO
) : Adapter<TrashNoteRecyclerAdapter.CustomViewHolder>() {
    private val trashNotes: ArrayList<NoteDataModelForRecycler> =
        databaseDAO.getNotes(DataBaseHelper.STATE_TRUE, DataBaseHelper.STATE_FALSE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder =
        CustomViewHolder(
            NoteTrashItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.setData(trashNotes[position])
    }

    override fun getItemCount(): Int = trashNotes.size

    inner class CustomViewHolder(
        private val binding: NoteTrashItemBinding
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
            binding.icDeleteNote.setOnClickListener {

            }
            binding.icRestoreNote.setOnClickListener {

            }
            // Intent to Note Activity To See And Edite Notes
            binding.secondRoot.setOnClickListener {
                context.startActivity(
                    Intent(context, NoteActivity::class.java)
                        .putExtra("IS_NEW_NOTE", false)
                        .putExtra("IS_TRASH_NOTE", true)
                        .putExtra(DataBaseHelper.NOTES_ID, noteDetails.noteId)
                )
            }
        }
    }
}