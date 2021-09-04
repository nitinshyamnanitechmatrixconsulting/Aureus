package com.auresus.academy.model.bean.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InvoiceListResponse(
/*{
  "totalAmount": 2.68,
      "subTotal": 2.50,
      "status": "Paid",
      "pdfUrl": "http://full-aureusgroup.cs117.force.com/apex/SubscriptionInvoiceHTML?id=a086F00002TThVCQA1",
      "pdfDownloadUrl": "http://full-aureusgroup.cs117.force.com/apex/SubscriptionInvoicePdfClone?id=a086F00002TThVCQA1",
      "payments": [
        {
          "paymentDate": "2019-10-03",
          "method": "Card",
          "amount": 2.68
        }
      ],
      "invoiceNumber": "InvNo-40259",
      "invoiceId": "a086F00002TThVCQA1",
      "invoiceDate": "2019-10-01",
      "gstTaxAmount": 0.18,
      "gstTax": 7.00,
      "grossTotal": 2.50,
      "dueDate": "2019-10-03",
      "discountAmount": 0.00,
      "Deposit": 0.00,
      "creditMemoAmount": null,
      "cardType": "Visa",
      "cardNumber": "2400",
      "balanceAmount": 2.68}*/
    var invoices: List<InvoiceList>
)

data class InvoiceList(
    val totalAmount: Double,
    val subTotal: Double,
    val status: String,
    @SerializedName("pdfUrl")
    val pdfURL: String,

    @SerializedName("pdfDownloadUrl")
    val pdfDownloadURL: String,

    val payments: List<Payment>,
    val invoiceNumber: String,

    @SerializedName("invoiceId")
    val invoiceID: String,

    val invoiceDate: String,
    val gstTaxAmount: Double,
    val gstTax: Double,
    val grossTotal: Double,
    val dueDate: String,
    val discountAmount: Double,

    @SerializedName("Deposit")
    val deposit: Double,

    val creditMemoAmount: Any? = null,
    val cardType: String,
    val cardNumber: String,
    val balanceAmount: Double
) : Serializable

data class Payment(
    val paymentDate: String,
    val method: String,
    val amount: Double
) : Serializable