package com.ir.ali.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.ir.ali.note.databinding.FragmentArchiveBinding
import com.ir.ali.note.databinding.FragmentTrashBinding

class ArchiveFragment : Fragment(R.layout.fragment_archive) {
    private lateinit var binding: FragmentArchiveBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // region Set Toolbar As Action Bar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        // endregion
        binding.toolBar.setNavigationOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}