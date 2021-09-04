package com.auresus.academy.view.invoice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.auresus.academy.R
import com.auresus.academy.databinding.ActivityInvoiceDetailsBinding
import com.auresus.academy.model.bean.responses.InvoiceList
import com.auresus.academy.utils.DateTimeUtil
import com.auresus.academy.view.base.BaseActivity


class InvoiceDetailsAcitivty : BaseActivity() {

    private var invocieItem: InvoiceList? = null
    private lateinit var binding: ActivityInvoiceDetailsBinding

    companion object {
        fun open(currActivity: BaseActivity, notification: InvoiceList) {
            currActivity.run {
                val bundle = Bundle()
                bundle.putSerializable("notificationItem", notification)
                val intent = Intent(this, InvoiceDetailsAcitivty::class.java)
                intent.putExtra("extra", bundle);
                startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_invoice_details
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.binding = binding as ActivityInvoiceDetailsBinding
        initClickListener()
        if (intent != null) {
            var bundle: Bundle = intent.getBundleExtra("extra")!!
            invocieItem = bundle.get("notificationItem") as InvoiceList?
        }
        if (invocieItem != null)
            setData()
    }

    private fun setData() {
        binding.toolbarTitle.text = invocieItem!!.invoiceNumber
        binding.invoiceNumberTv.text = invocieItem!!.invoiceNumber
        binding.invoiceDate.text = DateTimeUtil.invoiceDate(invocieItem!!.invoiceDate)
        binding.invoiceStatus.text = invocieItem!!.status
        binding.totalAmt.text = "SGD $${invocieItem!!.totalAmount}"
        binding.balanceAmt.text = "SGD $${invocieItem!!.balanceAmount}"
        binding.paymentDate.text = DateTimeUtil.invoiceDate(invocieItem!!.payments[0].paymentDate)
        binding.paymentAmount.text = "SGD $${invocieItem!!.payments[0].amount}"
        binding.paymentMethod.text = invocieItem!!.cardType + "x" + invocieItem!!.cardNumber
    }


    private fun initClickListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.viewPdf.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(invocieItem!!.pdfURL))
            startActivity(browserIntent)
        }
    }
}