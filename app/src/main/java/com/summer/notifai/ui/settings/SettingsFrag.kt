package com.summer.notifai.ui.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFrag : BaseFragment<FragSettingsBinding>() {

    override val layoutResId: Int
        get() = R.layout.frag_settings


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        mBinding.ivFragSettingsBack.setOnClickListener {
            requireActivity().finish()
        }
        mBinding.tvFragSettingsItemBlockList.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.settingsFrag)
                findNavController().navigate(SettingsFragDirections.actionSettingsFragToBlockListFrag())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}