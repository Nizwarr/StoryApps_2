package com.project.nizwar.storyapp.view.main

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.nizwar.storyapp.R
import com.project.nizwar.storyapp.data.Result
import com.project.nizwar.storyapp.data.model.Story
import com.project.nizwar.storyapp.databinding.ActivityMainBinding
import com.project.nizwar.storyapp.utils.ViewModelFactory
import com.project.nizwar.storyapp.view.detail.DetailActivity
import com.project.nizwar.storyapp.view.login.LoginActivity
import com.project.nizwar.storyapp.view.maps.MapsActivity
import com.project.nizwar.storyapp.view.post.PostActivity
import com.project.nizwar.storyapp.view.settings.SettingsActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvStory.layoutManager = layoutManager

        binding.fabPost.setOnClickListener { toPostActivity() }
        binding.fabMaps.setOnClickListener { toMapsActivity() }

        mainViewModel.getToken().observe(this) { token ->
            if (token != null) {
                showRecyclerList(token)
            }
        }
    }

    private fun showRecyclerList(token: String) {
        val listStoryAdapter = ListAdapter()
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvStory.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvStory.layoutManager = LinearLayoutManager(this)
        }

        binding.rvStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryAdapter.retry()
            }
        )

        mainViewModel.getAllStories(token).observe(this) {
            listStoryAdapter.submitData(lifecycle, it)
            if (it != null)  {
                showLoading(false)
            }
        }

        listStoryAdapter.setOnItemClickCallBack(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(story: Story, optionsCompat: ActivityOptionsCompat) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, story.id)
                startActivity(intent, optionsCompat.toBundle())
            }
        })

        binding.rvStory.adapter = listStoryAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.clearToken()
                toLoginActivity()
                true
            }
            R.id.settings -> {
                toSettingsActivity()
                true
            }
            else -> {
                true
            }
        }
    }

    private fun toSettingsActivity() {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun toPostActivity() {
        val intent = Intent(this@MainActivity, PostActivity::class.java)
        startActivity(intent)
    }

    private fun toLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toMapsActivity() {
        val intent = Intent(this@MainActivity, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}