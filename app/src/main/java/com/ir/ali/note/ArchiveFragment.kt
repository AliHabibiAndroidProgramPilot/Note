package com.ir.ali.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}