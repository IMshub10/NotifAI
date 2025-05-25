package com.summer.notifai.ui.settings.categories

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragClassifySmsTypeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassifySmsTypeFrag : BaseFragment<FragClassifySmsTypeBinding>() {

    override val layoutResId: Int = R.layout.frag_classify_sms_type

    private val viewModel: SmsTypeSettingsViewModel by viewModels()
    private lateinit var adapter: SmsTypeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SmsTypeListAdapter { entity, isImportant ->
            viewModel.updateImportance(entity.id, isImportant)
        }

        mBinding.rvFragBlockListList.adapter = adapter

        mBinding.ivFragBlockListBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.etFragBlockListSearch.addTextChangedListener {
            viewModel.updateQuery(it.toString())
        }

        lifecycleScope.launch {
            viewModel.smsTypeItems.collectLatest {
                adapter.submitList(it)
            }
        }
    }
}