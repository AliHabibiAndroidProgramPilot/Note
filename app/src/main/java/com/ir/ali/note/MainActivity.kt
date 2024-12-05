package com.ir.ali.note

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
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
        binding.icOpenDrawer.setOnClickListener { rootDrawerLayout.openDrawer(GravityCompat.START) }
        navigationDrawerToggle.syncState()
        binding.navigationView.setItemTextAppearanceActiveBoldEnabled(false)
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.NotesItem -> {
                    if (supportFragmentManager.fragments.isNotEmpty()) {
                        lifecycleScope.launch {
                            delay(450)
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .remove(supportFragmentManager.fragments.last())
                                .commit()
                            manageViewsVisibility(true)
                            supportFragmentManager.popBackStack(
                                null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        }
                    }
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    true
                }
                R.id.TrashItem -> {
                    if (supportFragmentManager.fragments.lastOrNull() !is TrashFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            manageViewsVisibility(false)
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, TrashFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    true
                }
                R.id.ArchiveItem -> {
                    if (supportFragmentManager.fragments.lastOrNull() !is ArchiveFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            manageViewsVisibility(false)
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, ArchiveFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    true
                }
                R.id.SettingItem -> {
                    if (supportFragmentManager.fragments.lastOrNull() !is SettingFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            manageViewsVisibility(false)
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, SettingFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    true
                }
                R.id.AboutItem -> {
                    if (supportFragmentManager.fragments.lastOrNull() !is AboutFragment) {
                        lifecycleScope.launch {
                            delay(450)
                            manageViewsVisibility(false)
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.animate_fragment_change_enter,
                                    R.anim.animate_fragment_change_exit
                                )
                                .replace(R.id.fragmentContainer, AboutFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    rootDrawerLayout.closeDrawer(GravityCompat.START, true)
                    true
                }
                else -> false
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.fragments.lastOrNull()) {
                is TrashFragment -> binding.navigationView.setCheckedItem(R.id.TrashItem)
                is ArchiveFragment -> binding.navigationView.setCheckedItem(R.id.ArchiveItem)
                is SettingFragment -> binding.navigationView.setCheckedItem(R.id.SettingItem)
                is AboutFragment -> binding.navigationView.setCheckedItem(R.id.AboutItem)
                null -> {
                    binding.navigationView.setCheckedItem(R.id.NotesItem)
                    recyclerAdapter.notifyRecycler(
                        databaseDao.getNotes(DataBaseHelper.STATE_FALSE, DataBaseHelper.STATE_FALSE)
                    )
                }
            }
            if (supportFragmentManager.fragments.isEmpty())
                manageViewsVisibility(true)
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
        binding.notesRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerAdapter = NoteRecyclerAdapter(this, databaseDao)
        binding.notesRecycler.adapter = recyclerAdapter
    }

    private fun showSnackBar() {
        Snackbar.make(
            binding.root,
            "Empty note discarded",
            Snackbar.LENGTH_SHORT
        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
    }

    private fun manageViewsVisibility(visible: Boolean) {
        if (visible) {
            binding.notesRecycler.visibility = View.VISIBLE
            binding.fabAddNote.visibility = View.VISIBLE
            binding.toolBar.visibility = View.VISIBLE
        }
        else {
            binding.notesRecycler.visibility = View.INVISIBLE
            binding.fabAddNote.visibility = View.INVISIBLE
            binding.toolBar.visibility = View.INVISIBLE
        }
    }
}