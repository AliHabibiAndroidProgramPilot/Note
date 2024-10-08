package com.ir.ali.note

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ir.ali.note.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val titleEditText = binding.edtNoteTitle
        titleEditText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "super.onBackPressed()",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onBackPressed() {
        Log.i("isWorking", "yes Working")
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.i("isWorking", "yes Working")
        onBackPressed()
        return true
    }
}