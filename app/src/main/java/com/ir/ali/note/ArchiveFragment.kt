package com.ir.ali.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ir.ali.note.adapters.ArchiveNoteRecyclerAdapter
import com.ir.ali.note.adapters.NoteRecyclerAdapter
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.FragmentArchiveBinding

class ArchiveFragment : Fragment(R.layout.fragment_archive) {
    private lateinit var binding: FragmentArchiveBinding
    private lateinit var databaseDao: NoteDAO
    private lateinit var archiveNoteRecyclerAdapter: ArchiveNoteRecyclerAdapter
    private lateinit var mainNotesRecycler: NoteRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(layoutInflater)
        val context = requireContext()
        databaseDao = NoteDAO(DataBaseHelper(context))
        mainNotesRecycler = NoteRecyclerAdapter(requireActivity(), databaseDao)
        archiveNoteRecyclerAdapter = ArchiveNoteRecyclerAdapter(context, databaseDao)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // region Set Toolbar As Action Bar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        // endregion
        initializeTrashRecycler()
        binding.toolBar.setNavigationOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeTrashRecycler() {
        val recyclerView = binding.archiveRecycler
        val fragmentContext = requireContext()
        recyclerView.layoutManager =
            LinearLayoutManager(fragmentContext, RecyclerView.VERTICAL, false)
        recyclerView.adapter = ArchiveNoteRecyclerAdapter(fragmentContext, databaseDao)
    }

    override fun onResume() {
        super.onResume()
        archiveNoteRecyclerAdapter.notifyRecycler(
            databaseDao.getNotes(DataBaseHelper.STATE_FALSE, DataBaseHelper.STATE_TRUE)
        )
    }
}