package com.auresus.academy.view.teacherhome

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auresus.academy.R
import com.auresus.academy.model.bean.TeacherBooking
import com.auresus.academy.model.bean.responses.Attachment
import com.auresus.academy.model.remote.ApiResponse
import com.auresus.academy.utils.Connectivity
import com.auresus.academy.view.base.BaseFragment
import com.auresus.academy.view_model.BaseViewModel
import com.auresus.academy.view_model.DashboardViewModel
import com.daimajia.swipe.util.Attributes
import kotlinx.android.synthetic.main.fragment_lesson_files.*
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileNotFoundException
import java.io.InputStream


class LessonAddFileFragment : BaseFragment() {
    private var booking: TeacherBooking? = null
    private val baseViewModel: BaseViewModel by viewModel()
    private val dashboardViewModel: DashboardViewModel by viewModel()
    private val OPEN_DOCUMENT_REQUEST_CODE = 2
    private var mUploadMessage: ValueCallback<Uri>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null

    val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1

    private val supportedMimeTypes: Array<String> =
        allSupportedDocumentsTypesToExtensions.keys.toTypedArray()
    private var mAdapter: RecyclerView.Adapter<*>? = null

    private var mDataSet: MutableList<Attachment> = mutableListOf()

    companion object {
        val TAG = "LessonAddFileFragment"
        val EXTRA_TEACHER_BOOKING = "EXTRA_TEACHER_BOOKING"
        fun newInstance(teacherBooking: TeacherBooking?): LessonAddFileFragment {
            val accountFragment = LessonAddFileFragment().apply {
                arguments = Bundle().apply { putParcelable(EXTRA_TEACHER_BOOKING, teacherBooking) }
            }
            return accountFragment
        }
    }

    private val bookingListObserver: androidx.lifecycle.Observer<ApiResponse<List<Attachment>>> by lazy {
        androidx.lifecycle.Observer<ApiResponse<List<Attachment>>> {
            handleBookingListResponse(it)
        }
    }


    private fun handleBookingListResponse(response: ApiResponse<List<Attachment>>) {
        when (response.status) {
            ApiResponse.Status.LOADING -> {

            }
            ApiResponse.Status.SUCCESS -> {
                setList(response.data)
            }
            ApiResponse.Status.ERROR -> {
                if (response.error?.code == 500)
                    Toast.makeText(activity, response.error?.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(
                        activity,
                        getString(R.string.internal_server_error),
                        Toast.LENGTH_LONG
                    ).show()

            }
        }
    }

    private fun setList(data: List<Attachment>?) {
        data?.let {
            mDataSet.clear()
            mDataSet?.addAll(it)
            mAdapter?.notifyDataSetChanged()
        }

    }

    private fun setAttachmentListAObserver() {
        if (Connectivity.isConnected(activity)) {
            dashboardViewModel.attachmentListResponse.observe(this, bookingListObserver)
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_lesson_add_files
    }

    override fun onViewsInitialized(binding: ViewDataBinding, view: View) {
        setRecyclerView()
        booking = arguments?.get(EXTRA_TEACHER_BOOKING) as TeacherBooking
        val isOnline = booking?.Lesson_Type__c == "Online"
        if (!isOnline) view.findViewById<LinearLayout>(R.id.toolbar).visibility = View.GONE
        backButton.visibility = View.GONE
        setAttachmentListAObserver()
        initWebView()
        callAttachmentList()
    }

    private fun callAttachmentList() {
        booking?.let {
            it.Online_Lesson_URL__c?.let {
                val uri = Uri.parse(booking?.Online_Lesson_URL__c)
                val action = uri.getQueryParameter("room_name")
                action?.let {
                    dashboardViewModel.getAttachmentList(action)
                }
            }
        }
    }

    private fun initWebView() {
        booking?.let {
            val roomName = it.Id
            val url =
                "https://full-aureusgroup.cs117.force.com/AureusFileUploadPageFromIpad?Color=black&id=$roomName"
            simpleWebView.setClickable(true);
            simpleWebView.getSettings().setJavaScriptEnabled(true)
            simpleWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            simpleWebView.getSettings().setUseWideViewPort(false);
            simpleWebView.loadUrl(url)
            simpleWebView.webViewClient = object : WebViewClient() {
                private fun handleUrl(url: String?) {
                    if (!isConsentFormUrl(url)) {
                        return
                    }
                    val uri = Uri.parse(url)
                    val action = uri.getQueryParameter("upload")

                    when (action) {
                        "true" -> handleUploadComplete()
                        else -> {
                        }
                    }
                }


                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest
                ): Boolean {
                    val url = request.url.toString()
                    if (isConsentFormUrl(url)) {
                        handleUrl(url)
                        return true
                    }
                    return false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (isConsentFormUrl(url)) {
                        handleUrl(url)
                        return true
                    }
                    return false
                }


                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                }
            }
            simpleWebView.setWebChromeClient(object : WebChromeClient() {

                // For 3.0+ Devices (Start)
                // onActivityResult attached before constructor
                fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
                    mUploadMessage = uploadMsg
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = "*/*"
                    startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE
                    )
                }


                //For Android 4.1 only
                fun openFileChooser(
                    uploadMsg: ValueCallback<Uri>,
                    acceptType: String,
                    capture: String
                ) {
                    mUploadMessage = uploadMsg
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "*/*"
                    startActivityForResult(
                        Intent.createChooser(intent, "File Browser"),
                        FILECHOOSER_RESULTCODE
                    )
                }

                fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                    mUploadMessage = uploadMsg
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = "*/*"
                    startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE
                    )
                }

                // For Lollipop 5.0+ Devices
                override fun onShowFileChooser(
                    mWebView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: WebChromeClient.FileChooserParams
                ): Boolean {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (uploadMessage != null) {
                            uploadMessage?.onReceiveValue(null)
                            uploadMessage = null
                        }
                        uploadMessage = filePathCallback
                        val intent = fileChooserParams.createIntent()
                        try {
                            startActivityForResult(intent, REQUEST_SELECT_FILE)
                        } catch (e: ActivityNotFoundException) {
                            uploadMessage = null
                            Toast.makeText(activity, "Cannot Open File Chooser", Toast.LENGTH_LONG)
                                .show()
                            return false
                        }
                        return true
                    } else {
                        return false
                    }
                }

            })
        }
    }

    private fun handleUploadComplete() {
        callAttachmentList()
    }

    private fun isConsentFormUrl(url: String?): Boolean {
        var isConsentUrl = false;
        url?.let {
            if (!(url.isNullOrEmpty() && url.contains("upload"))) {
                isConsentUrl = true
            }
        }
        return isConsentUrl

    }


    private fun setRecyclerView() {
        recycler_view.setLayoutManager(LinearLayoutManager(activity))
        mAdapter = RecyclerViewAdapter(activity, mDataSet)
        (mAdapter as RecyclerViewAdapter).mode = Attributes.Mode.Single
        recycler_view.setAdapter(mAdapter)
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(activity as FragmentActivity, R.drawable.item_divider)?.let {
            itemDecorator.setDrawable(it)
            recycler_view.addItemDecoration(itemDecorator)
        }

    }

    fun Fragment.openDocumentPicker() {
        val openDocumentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes)
        }

        startActivityForResult(openDocumentIntent, OPEN_DOCUMENT_REQUEST_CODE)
    }

    fun Fragment.tryHandleOpenDocumentResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): OpenFileResult {
        return if (requestCode == OPEN_DOCUMENT_REQUEST_CODE) {
            handleOpenDocumentResult(resultCode, data)
        } else OpenFileResult.DifferentResult
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage != null) {
                    uploadMessage?.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                    )
                    uploadMessage = null
                }
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage != null) {
                var result = data?.data
                mUploadMessage?.onReceiveValue(result)
                mUploadMessage = null
            }
        } else {
            Toast.makeText(
                activity,
                "Failed to open file uploader, please check app permissions.",
                Toast.LENGTH_LONG
            ).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun callbackWebViewUpload(fileName: String, content: InputStream) {
        mUploadMessage?.onReceiveValue(Uri.parse(fileName));
        mUploadMessage = null;

    }

    private fun Fragment.handleOpenDocumentResult(resultCode: Int, data: Intent?): OpenFileResult {
        return if (resultCode == Activity.RESULT_OK && data != null) {
            val contentUri = data.data
            if (contentUri != null) {
                val stream =
                    try {
                        requireActivity().application.contentResolver.openInputStream(contentUri)
                    } catch (exception: FileNotFoundException) {
                        Log.e("LessonDetailsFragment", exception.message!!)
                        return OpenFileResult.ErrorOpeningFile
                    }

                val fileName = requireContext().contentResolver.queryFileName(contentUri)

                if (stream != null && fileName != null) {
                    OpenFileResult.FileWasOpened(fileName, stream)
                } else OpenFileResult.ErrorOpeningFile
            } else {
                OpenFileResult.ErrorOpeningFile
            }
        } else {
            OpenFileResult.OpenFileWasCancelled
        }
    }


    sealed class OpenFileResult {
        object OpenFileWasCancelled : OpenFileResult()
        data class FileWasOpened(val fileName: String, val content: InputStream) : OpenFileResult()
        object ErrorOpeningFile : OpenFileResult()
        object DifferentResult : OpenFileResult()
    }
}