package com.summer.notifai.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.summer.core.domain.model.SearchSectionId
import com.summer.core.ui.BaseActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_search

    private var _navController: NavController? = null
    private val navController
        get() = _navController!!

    private var isGlobalSearch: Boolean = true
    private var senderAddressId: Long = 0L

    override fun onActivityReady(savedInstanceState: Bundle?) {
        initIntent()
        setupNavController(if (isGlobalSearch) R.id.globalSearchFrag else R.id.searchListFrag)
    }

    private fun initIntent() {
        isGlobalSearch = intent.getBooleanExtra(KEY_IS_GLOBAL_SEARCH, true)
        senderAddressId = intent.getLongExtra(KEY_SENDER_ADDRESS_ID, 0L)
    }

    private fun setupNavController(startDestination: Int) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_actSearch_navHost) as? NavHostFragment
        _navController =
            navHostFragment?.navController ?: throw IllegalStateException("NavController is null")

        val navGraph = navController.navInflater.inflate(R.navigation.nav_search)
        navGraph.setStartDestination(startDestination)

        if (startDestination == R.id.searchListFrag) {
            val args = Bundle().apply {
                putString("query", "")
                putString("searchType", SearchSectionId.MESSAGES.id.toString())
                putString("senderAddressId", senderAddressId.toString())
            }

            navController.setGraph(navGraph, args)
        } else {
            navController.graph = navGraph
        }
    }

    companion object {
        private const val KEY_IS_GLOBAL_SEARCH = "is_global_search"
        private const val KEY_SENDER_ADDRESS_ID = "sender_address_id"

        fun onNewInstance(
            context: Context,
            isGlobalSearch: Boolean = true,
            senderAddressId: Long = 0L
        ): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra(KEY_IS_GLOBAL_SEARCH, isGlobalSearch)
                putExtra(KEY_SENDER_ADDRESS_ID, senderAddressId)
            }
        }
    }

}