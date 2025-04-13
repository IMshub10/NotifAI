package com.summer.notifai.ui.search

import android.os.Bundle
import com.summer.core.ui.BaseActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_search

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

}