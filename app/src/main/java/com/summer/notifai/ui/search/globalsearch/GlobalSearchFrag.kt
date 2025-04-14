package com.summer.notifai.ui.search.globalsearch

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragGlobalSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GlobalSearchFrag : BaseFragment<FragGlobalSearchBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_global_search

    private val viewModel: GlobalSearchViewModel by viewModels()

    private var _searchAdapter: GlobalSearchAdapter? = null
    private val searchAdapter get() = _searchAdapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchAdapter()
        observeSearchResults()
        initSearchView()
        listeners()
    }

    private fun setupSearchAdapter() {
        _searchAdapter = GlobalSearchAdapter()
        mBinding.rvFragGlobalSearchList.adapter = searchAdapter
        mBinding.rvFragGlobalSearchList.itemAnimator?.apply {
            addDuration = 120
            removeDuration = 120
            changeDuration = 100
            moveDuration = 100
        }
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { items ->
            searchAdapter.updateQuery(viewModel.searchFilter.value.orEmpty())
            searchAdapter.submitList(items)
        }
    }

    private fun listeners() {
        mBinding.ivFragGlobalSearchBack.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.globalSearchFrag)
                findNavController().popBackStack()
        }
        mBinding.etFragGlobalSearchSearch.addTextChangedListener {
            viewModel.searchFilter.value = it.toString()
        }
    }

    private fun initSearchView() {
        mBinding.etFragGlobalSearchSearch.post {
            mBinding.etFragGlobalSearchSearch.requestFocus()
        }
    }

    override fun onDestroyView() {
        _searchAdapter = null
        super.onDestroyView()
    }
}