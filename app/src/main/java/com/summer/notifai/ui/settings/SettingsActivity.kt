package com.summer.notifai.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.summer.core.ui.BaseActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    override val layoutResId: Int
        get() = R.layout.activity_settings

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupNavController()
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fcv_actSettings_navHost) as? NavHostFragment
        _navController = navHostFragment?.navController
            ?: throw IllegalStateException("NavController is null")

        val navGraph = navController.navInflater.inflate(R.navigation.nav_settings)
        navController.graph = navGraph
    }

    companion object {
        fun onNewInstance(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}