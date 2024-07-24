package com.stack

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class DashBoard : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dash_board)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout!!.addDrawerListener(toggle)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_todos -> {
                    // Handle the Home action
                }

                R.id.nav_notes -> {
                    // Handle the Settings action
                }


            }
            drawerLayout!!.closeDrawers()
            true
        }
        if (savedInstanceState == null) {

            navigationView.setCheckedItem(R.id.nav_todos);
        }

        val menu = navigationView.menu
        menu.add(R.id.label_grp, 1000, Menu.NONE, "Label 1").setIcon(R.drawable.baseline_label_24).setCheckable(true)
        menu.add(R.id.label_grp, 1000, Menu.NONE, "Create new label").setIcon(R.drawable.baseline_new_label_24)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_toolbar_menus, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                // You can perform the search and update your UI here
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                // You can update your search results dynamically here
                return false
            }
        })

        return true
    }


}