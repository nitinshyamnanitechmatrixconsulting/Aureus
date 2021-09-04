package com.auresus.academy.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import com.auresus.academy.databinding.DialogEnterOtpBinding
import com.auresus.academy.databinding.DialogInfoBinding
import com.auresus.academy.databinding.DialogInfoCancelBinding

object DialogUtils {

    fun showAlertDialog(
        activity: Activity,
        text: String
    ) {
        val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity.layoutInflater
        val dialogView: DialogInfoBinding = DialogInfoBinding.inflate(inflater)
        dialogView.dialogBody.text = text
        dialogView.dialogButtonCancel.setOnClickListener { dialog.dismiss() }
        dialog.setView(dialogView.root)
        dialog.show()
    }

    fun showAlertDialogCallback(
        activity: Activity,
        text: String , listener: OkDialogClickListener
    ) {
        val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity.layoutInflater
        val dialogView: DialogInfoBinding = DialogInfoBinding.inflate(inflater)
        dialogView.dialogBody.text = text
        dialogView.dialogButtonCancel.setOnClickListener { listener.onOkClick(dialog)}
        dialog.setView(dialogView.root)
        dialog.show()
    }

    fun showAlertDialogInfoCancleCallback(
        activity: Activity,
        text: String , listener: OkDialogClickListener
    ) {
        val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity.layoutInflater
        val dialogView: DialogInfoCancelBinding = DialogInfoCancelBinding.inflate(inflater)
        dialogView.dialogBody.text = text
        dialogView.dialogButtonok.setOnClickListener { listener.onOkClick(dialog)}
        dialogView.dialogButtonCancel.setOnClickListener { dialog.dismiss()}
        dialog.setView(dialogView.root)
        dialog.show()
    }

    fun showEnterOtp(
        activity: Activity,
        text: String, listener: DialogClickListener
    ) {
        val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity.layoutInflater
        val dialogView: DialogEnterOtpBinding = DialogEnterOtpBinding.inflate(inflater)
        dialogView.dialogBody.text = text
        dialogView.dialogButtonCancel.setOnClickListener { dialog.dismiss() }
        dialogView.dialogButtonSubmit.setOnClickListener {
            listener.onOkClick(
                dialog,
                dialogView.entetOtp.text.toString()
            )
        }
        dialogView.resendOtp.setOnClickListener { listener.resendOtp(dialog) }
        dialog.setView(dialogView.root)
        dialog.show()
    }
}

interface DialogClickListener {
    fun onOkClick(dialog: Dialog, otp: String)
    fun resendOtp(dialog: Dialog)
}


interface OkDialogClickListener {
    fun onOkClick(dialog: Dialog)
}
