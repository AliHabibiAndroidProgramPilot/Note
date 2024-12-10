package com.ir.ali.note.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ir.ali.note.NoteActivity
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.NoteArchvieItemBinding
import com.ir.ali.note.datamodel.NoteDataModelForRecycler

class ArchiveNoteRecyclerAdapter(
    private val context: Context,
    private val databaseDao: NoteDAO
) : Adapter<ArchiveNoteRecyclerAdapter.CustomViewHolder>() {
    private var archiveNotes: ArrayList<NoteDataModelForRecycler> =
        databaseDao.getNotes(DataBaseHelper.STATE_FALSE, DataBaseHelper.STATE_TRUE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder =
        CustomViewHolder(
            NoteArchvieItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.setData(archiveNotes[position])
    }

    override fun getItemCount(): Int = archiveNotes.size

    @SuppressLint("NotifyDataSetChanged")
    fun notifyRecycler(changedData: ArrayList<NoteDataModelForRecycler>) {
        archiveNotes.clear()
        archiveNotes.addAll(changedData)
        //  Relying On notifyDataSetChanged As A Last Resort
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(
        private val binding: NoteArchvieItemBinding
    ) : ViewHolder(binding.root) {
        fun setData(noteDetails: NoteDataModelForRecycler) {
            binding.txtNoteTitle.text = noteDetails.noteTitle
            binding.txtNoteText.text = noteDetails.noteText
            binding.txtNoteDate.text = noteDetails.noteDate
            binding.icUnarchive.setOnClickListener{
                // TODO UNARCHIVE FROM LIST AND DATABASE
            }
            binding.secondRoot.setOnClickListener {
                context.startActivity(
                    Intent(context, NoteActivity::class.java)
                        // Specifies Archive Notes So To Manage Them
                        .putExtra(DataBaseHelper.NOTES_ID, noteDetails.noteId)
                        .putExtra("IS_ARCHIVE_NOTE", true)
                )
            }
        }
    }
}