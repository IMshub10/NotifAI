package com.summer.notifai.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

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
        mBinding.tvFragSettingsItemSmsCategories.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.settingsFrag)
                findNavController().navigate(SettingsFragDirections.actionSettingsFragToSmsTypeSettingsFrag())
        }
        mBinding.tvFragSettingsItemPrivacy.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.settingsFrag){
                val url = "https://github.com/IMshub10/NotifAI/wiki"
                startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}