package com.summer.notifai.ui.search

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.summer.core.ui.BaseActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_search

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupNavController(R.id.globalSearchFrag)
    }

    private fun setupNavController(startDestination: Int) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_actSearch_navHost) as? NavHostFragment
        val navController =
            navHostFragment?.navController ?: throw IllegalStateException("NavController is null")

        val navGraph = navController.navInflater.inflate(R.navigation.nav_search)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

}