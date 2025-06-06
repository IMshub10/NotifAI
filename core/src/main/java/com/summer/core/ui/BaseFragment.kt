package com.summer.core.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    @get:LayoutRes
    protected abstract val layoutResId: Int

    private var binding: B? = null
    protected val mBinding: B
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                layoutResId,
                container,
                false
            )
        onFragmentReady(savedInstanceState)
        return mBinding.root
    }

    open fun onFragmentReady(instanceState: Bundle?){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}