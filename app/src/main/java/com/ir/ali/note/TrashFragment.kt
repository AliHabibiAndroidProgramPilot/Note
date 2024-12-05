package com.ir.ali.note

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ir.ali.note.adapters.TrashNoteRecyclerAdapter
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.FragmentTrashBinding

class TrashFragment : Fragment(R.layout.fragment_trash) {
    private lateinit var binding: FragmentTrashBinding
    private lateinit var databaseDao: NoteDAO
    private lateinit var trashNoteAdapter: TrashNoteRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrashBinding.inflate(layoutInflater)
        databaseDao = NoteDAO(DataBaseHelper(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // region Set Toolbar as ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        // endregion
        initializeTrashRecycler()
        // Handle BackPress
        binding.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeTrashRecycler() {
        val fragmentContext = requireContext()
        val dao = NoteDAO(DataBaseHelper(fragmentContext))
        trashNoteAdapter = TrashNoteRecyclerAdapter(fragmentContext, dao)
        binding.trashRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.trashRecycler.adapter = trashNoteAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.trash_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.RestoreAllItems -> {

            }
            R.id.DeleteAllItems -> {
                databaseDao.deleteAllTrashNotes()
                trashNoteAdapter.notifyRecycler()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}