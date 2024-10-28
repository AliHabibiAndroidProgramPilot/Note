package com.ir.ali.note

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
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
    private lateinit var databaseDao: NoteDAO
    private lateinit var recyclerAdapter: NoteRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeNoteRecycler()
        binding.fabAddNote.setOnClickListener {
            // Intent to Note Activity
            startActivity(
                Intent(this, NoteActivity::class.java)
                    .putExtra("IS_NEW_NOTE", true)
            )
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
        val rootDrawerLayout = binding.rootDrawerLayout
        val navigationDrawerToggle =
            ActionBarDrawerToggle(
                this,
                rootDrawerLayout,
                R.string.open,
                R.string.close
            )
        navigationDrawerToggle.isDrawerIndicatorEnabled = true
        navigationDrawerToggle.isDrawerSlideAnimationEnabled = true
        rootDrawerLayout.addDrawerListener(navigationDrawerToggle)
        navigationDrawerToggle.syncState()
        binding.icOpenDrawer.setOnClickListener { rootDrawerLayout.openDrawer(GravityCompat.START) }
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("SNACK_BAR", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("SHOW_SNACK_BAR", false)) {
            showSnackBar()
            // Clear the shared preferences flag After Showing SnackBar
            val editor = sharedPreferences.edit()
            editor.remove("SHOW_SNACK_BAR")
            editor.apply()
        }
        recyclerAdapter.notifyRecycler(
            databaseDao.getNotes(DataBaseHelper.STATE_FALSE, DataBaseHelper.STATE_FALSE)
        )
    }

    private fun initializeNoteRecycler() {
        databaseDao = NoteDAO(DataBaseHelper((this)))
        val noteList = databaseDao.getNotes(DataBaseHelper.STATE_FALSE, DataBaseHelper.STATE_FALSE)
        binding.notesRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerAdapter = NoteRecyclerAdapter(this, noteList, databaseDao)
        binding.notesRecycler.adapter = recyclerAdapter
    }

    private fun showSnackBar() {
        Snackbar.make(
            binding.root,
            "Empty note discarded",
            Snackbar.LENGTH_SHORT
        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
    }
}