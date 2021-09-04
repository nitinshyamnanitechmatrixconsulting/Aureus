package com.auresus.academy.view.studenthome;

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.auresus.academy.R
import com.auresus.academy.model.bean.Event
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder


class SimpleAdapter(val context: Context) : BaseBannerAdapter<Event>() {

    override fun bindData(
        holder: BaseViewHolder<Event>,
        event: Event?,
        position: Int,
        pageSize: Int
    ) {
        val imageStart: ImageView = holder.findViewById(R.id.slideImageView)
        val textTitle: TextView = holder.findViewById(R.id.titleTextView)
        val textDesription: TextView = holder.findViewById(R.id.descriptionTextView)
        val viewButton: Button = holder.findViewById(R.id.viewButton)
        event?.imageURL?.let { imageUrl-> Glide.with(context).load(imageUrl).into(imageStart)
        }
        textTitle.setText(event?.title)
        textDesription.setText(event?.description)
        viewButton.setOnClickListener {
            navigateToLink(event)
        }


    }

    private fun navigateToLink(event: Event?) {
        event?.webURL?.let {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            context?.startActivity(browserIntent)
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_slide;
    }
}
