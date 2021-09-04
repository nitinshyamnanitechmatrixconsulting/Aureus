package com.auresus.academy.view.invoice

import com.auresus.academy.model.bean.responses.InvoiceList

interface IInvoiceItemListener {
    fun itemClick(notificationItem: InvoiceList)
}