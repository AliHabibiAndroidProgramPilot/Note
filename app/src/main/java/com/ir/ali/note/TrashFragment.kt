package com.ir.ali.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ir.ali.note.databinding.FragmentTrashBinding

class TrashFragment : Fragment(R.layout.fragment_trash) {
    private lateinit var binding: FragmentTrashBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrashBinding.inflate(layoutInflater)
        return binding.root
    }
}