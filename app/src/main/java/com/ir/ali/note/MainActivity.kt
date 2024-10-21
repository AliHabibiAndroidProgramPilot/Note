package com.ir.ali.note

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ir.ali.note.adapters.NoteRecyclerAdapter
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabAddNote.setOnClickListener {
            // Intent to Note Activity
            startActivity(Intent(this, NoteActivity::class.java))
            //region Intent With Animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_OPEN,
                    R.anim.animate_activity_enter,
                    R.anim.animate_activity_exit
                )
            } else {
                this@MainActivity.overridePendingTransition(
                    R.anim.animate_activity_enter, R.anim.animate_activity_exit
                )
            }
            //endregion
        }
    }

    override fun onStart() {
        super.onStart()
        initializeNoteRecycler()
    }

    override fun onResume() {
        super.onResume()
        showSnackBar()
    }

    private fun initializeNoteRecycler() {
        val dataBaseDAO = NoteDAO(DataBaseHelper((this)))
        val noteList = dataBaseDAO.getNotes(DataBaseHelper.STATE_FALSE, DataBaseHelper.STATE_FALSE)
        binding.notesRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        binding.notesRecycler.adapter = NoteRecyclerAdapter(this, noteList)
    }

    private fun showSnackBar() {
        val sharedPreferences = getSharedPreferences("SNACK_BAR", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("SHOW_SNACK_BAR", false)) {
            Snackbar.make(
                binding.root,
                "Empty note discarded",
                Snackbar.LENGTH_SHORT
            ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
            // Clear the shared preferences flag
            val editor = sharedPreferences.edit()
            editor.remove("SHOW_SNACK_BAR")
            editor.apply()
        }
    }
}