package com.ir.ali.note.adapters

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ir.ali.note.databinding.NoteListItemBinding

class NoteRecyclerAdapter(
    private val contextActivity: Activity,
    private val notes: ArrayList<NoteDataModel>
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

    inner class CustomViewHolder(
        private val binding: NoteListItemBinding
    ) : ViewHolder(binding.root) {
        fun setData(noteDetails: NoteDataModel) {
            binding.txtNoteTitle.text = noteDetails.noteTitle
            binding.txtNoteText.text = noteDetails.noteText
            binding.txtNoteDate.text = noteDetails.noteDate

            //Manage Delete Icon Functionality
            binding.icDeleteNote.setOnClickListener {

            }
        }
    }
}