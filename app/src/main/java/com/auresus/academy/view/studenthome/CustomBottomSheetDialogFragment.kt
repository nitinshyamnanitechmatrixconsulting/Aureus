package com.auresus.academy.view.studenthome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auresus.academy.databinding.BottomSheetEnrollemntBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {


    lateinit var binding: BottomSheetEnrollemntBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEnrollemntBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickLiseneer()
    }

    private fun initClickLiseneer() {
        binding.enrllmentDetails.setOnClickListener {

        }
    }

}