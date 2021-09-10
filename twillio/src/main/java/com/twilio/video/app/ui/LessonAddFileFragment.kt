package com.twilio.video.app.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.util.Attributes
import com.twilio.video.app.R
import com.twilio.video.app.data.api.model.ApiResponse
import com.twilio.video.app.ui.room.RoomViewModel
import com.twilio.video.app.util.Connectivity
import com.twilio.video.app.util.allSupportedDocumentsTypesToExtensions
import com.twilio.video.app.util.queryFileName
import java.io.FileNotFoundException
import java.io.InputStream


class LessonAddFileFragment( val dashboardViewModel: RoomViewModel) : Fragment() {
    private var recycler_view: RecyclerView?=null
    private var backButton: ImageView?=null
    private var simpleWebView: WebView?=null
    private val OPEN_DOCUMENT_REQUEST_CODE = 2
    private var mUploadMessage: ValueCallback<Uri>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private lateinit var room_id:String
    private lateinit var room_name:String

    val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1

    private val supportedMimeTypes: Array<String> =
        allSupportedDocumentsTypesToExtensions.keys.toTypedArray()
    private var mAdapter: RecyclerView.Adapter<*>? = null

    private var mDataSet: MutableList<Attachment> = mutableListOf()

    companion object {
        val TAG = "LessonAddFileFragment"
        val EXTRA_ROOM_ID = "EXTRA_ROOM_ID"
        val EXTRA_ROOM_NAME= "EXTRA_ROOM_NAME"
        fun newInstance(roomName:String,room_id:String,roomViewModel: RoomViewModel): LessonAddFileFragment {
            val accountFragment = LessonAddFileFragment(roomViewModel).apply {
                arguments = Bundle().apply {
                    putString(EXTRA_ROOM_NAME, roomName)
                    putString(EXTRA_ROOM_ID, room_id) }
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
            dashboardViewModel.attachmentListResponse.observe(viewLifecycleOwner, bookingListObserver)
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.no_network_error),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lesson_add_files,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewsInitialized()

    }
    fun onViewsInitialized() {
        simpleWebView = view?.findViewById<WebView>(R.id.simpleWebView)
        recycler_view = view?.findViewById<RecyclerView>(R.id.recycler_view)
        backButton = view?.findViewById<ImageView>(R.id.backButton)
        setRecyclerView()
        room_id = arguments?.get(EXTRA_ROOM_ID) as String
        room_name = arguments?.get(EXTRA_ROOM_NAME) as String
        setAttachmentListAObserver()
        initWebView()
        callAttachmentList()

       backButton!!.setOnClickListener(View.OnClickListener { view ->

            getActivity()?.onBackPressed()
            // Do some work here
        })

    }

    private fun callAttachmentList() {
        dashboardViewModel.getAttachmentList(room_name)

    }

    private fun initWebView() {
            val url =
                "https://full-aureusgroup.cs117.force.com/AureusFileUploadPageFromIpad?Color=black&id=$room_id"
            simpleWebView?.setClickable(true);
            simpleWebView?.getSettings()?.setJavaScriptEnabled(true)
            simpleWebView?.getSettings()?.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            simpleWebView?.getSettings()?.setUseWideViewPort(false);
            simpleWebView?.loadUrl(url)
            simpleWebView?.webViewClient = object : WebViewClient() {
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
            simpleWebView?.setWebChromeClient(object : WebChromeClient() {

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
        recycler_view?.setLayoutManager(LinearLayoutManager(activity))
        mAdapter = RecyclerViewAdapter(activity, mDataSet)
        (mAdapter as RecyclerViewAdapter).mode = Attributes.Mode.Single
        recycler_view?.setAdapter(mAdapter)
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(activity as FragmentActivity, R.drawable.item_divider)?.let {
            itemDecorator.setDrawable(it)
            recycler_view?.addItemDecoration(itemDecorator)
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
                        Log.e("LessonDetailsFragment", exception.message?:"Failed opening document")
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