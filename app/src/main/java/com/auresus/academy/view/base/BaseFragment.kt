package com.auresus.academy.view.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.auresus.academy.model.bean.Student


abstract class BaseFragment : Fragment() {

    private lateinit var binding: ViewDataBinding

    abstract fun getLayoutId(): Int

    abstract fun onViewsInitialized(binding: ViewDataBinding, view: View)

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewsInitialized(binding, view)
        super.onViewCreated(view, savedInstanceState)
    }



}