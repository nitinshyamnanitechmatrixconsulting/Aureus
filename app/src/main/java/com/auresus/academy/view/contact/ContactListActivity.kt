package com.auresus.academy.view.contact

import android.content.Intent
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityContactListingBinding
import com.auresus.academy.model.bean.Contact
import com.auresus.academy.view.base.BaseActivity


/* Created by Sahil Bharti on 9/5/19.
 *
*/
class ContactListActivity : BaseActivity() {

    private lateinit var binding: ActivityContactListingBinding
    private lateinit var mAdapter: ContactListAdapter
    private var arrList = ArrayList<Contact>()

    companion object {

        fun open(currActivity: BaseActivity, arrayList: ArrayList<Contact>) {
            currActivity.run {
                startActivity(Intent(this, ContactListActivity::class.java).apply {
                    putExtra("arrayContact", arrayList)
                })
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_contact_listing
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityContactListingBinding
        getExtrasData()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvContacts.layoutManager = LinearLayoutManager(this)
        mAdapter = ContactListAdapter(this, arrList)
        binding.rvContacts.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    private fun getExtrasData() {
        val extraList = intent.getSerializableExtra("arrayContact") as ArrayList<Contact>
        arrList.addAll(extraList)
    }


}