package com.ir.ali.note

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ir.ali.note.adapters.NoteRecyclerAdapter
import com.ir.ali.note.database.DataBaseHelper
import com.ir.ali.note.database.NoteDAO
import com.ir.ali.note.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            ActionBarDrawerToggle(this, rootDrawerLayout, R.string.open, R.string.close)
        navigationDrawerToggle.isDrawerIndicatorEnabled = true
        navigationDrawerToggle.isDrawerSlideAnimationEnabled = true
        rootDrawerLayout.addDrawerListener(navigationDrawerToggle)
        navigationDrawerToggle.syncState()
        binding.icOpenDrawer.setOnClickListener { rootDrawerLayout.openDrawer(GravityCompat.START) }
        binding.navigationView.setNavigationItemSelectedListener {
            val recyclerView = binding.notesRecycler
            when(it.itemId) {
                R.id.NotesItem -> {
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    if (!recyclerView.isVisible) {
                        lifecycleScope.launch {
                            delay(450)
                            recyclerView.visibility = View.VISIBLE
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .remove(Fragment())
                                .commit()
                        }
                    }
                    true
                }
                R.id.TrashItem -> {
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    val currentFragment = supportFragmentManager.fragments.lastOrNull()
                    if (currentFragment !is TrashFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            recyclerView.visibility = View.INVISIBLE
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, TrashFragment())
                                .commit()
                        }
                    }
                    true
                }
                R.id.ArchiveItem -> {
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    val currentFragment = supportFragmentManager.fragments.lastOrNull()
                    if (currentFragment !is ArchiveFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            recyclerView.visibility = View.INVISIBLE
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, ArchiveFragment())
                                .commit()
                        }
                    }
                    true
                }
                R.id.SettingItem -> {
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    val currentFragment = supportFragmentManager.fragments.lastOrNull()
                    if (currentFragment !is SettingFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            recyclerView.visibility = View.INVISIBLE
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, SettingFragment())
                                .commit()
                        }
                    }
                    true
                }
                R.id.AboutItem -> {
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    val currentFragment = supportFragmentManager.fragments.lastOrNull()
                    if (currentFragment !is AboutFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            recyclerView.visibility = View.INVISIBLE
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, AboutFragment())
                                .commit()
                        }
                    }
                    true
                }
                else -> false
            }
        }
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