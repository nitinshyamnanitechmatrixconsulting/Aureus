package com.auresus.academy.view.teacherhome;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.auresus.academy.R;
import com.auresus.academy.model.bean.responses.Attachment;
import com.auresus.academy.model.remote.ApiHelper;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {
    private Context mContext;
    private ArrayList<Attachment> mDataset;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context, List<Attachment> objects) {
        this.mContext = context;
        this.mDataset = (ArrayList<Attachment>) objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_lesson_add_file, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        Attachment item = mDataset.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
/*
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                //Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
                showDialog("https://full-aureusgroup.cs117.force.com/servlet/servlet.FileDownload?file="+item.getFileID());
            }
        });
*/
        viewHolder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("https://full-aureusgroup.cs117.force.com/servlet/servlet.FileDownload?file="+item.getFileID());
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAttachment(viewHolder,position,item.getFileID());
             /*   mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewFileName.getText().toString() + "!", Toast.LENGTH_SHORT).show();*/
            }
        });
        viewHolder.ivFileType.setImageResource(R.drawable.ic_file_present);
        viewHolder.ivFileType.setColorFilter(ContextCompat.getColor(mContext, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        viewHolder.textViewFileName.setText(item.getFileName());
        viewHolder.textViewFileSize.setText(item.getFileSize());
        mItemManger.bindView(viewHolder.itemView, position);
        viewHolder.textViewFileName.setTextColor(mContext.getResources().getColor(R.color.black));
        viewHolder.textViewFileSize.setTextColor(mContext.getResources().getColor(R.color.black));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView ivFileType;
        TextView textViewFileName;
        TextView textViewFileSize;
        LinearLayout buttonDelete;
        RelativeLayout rlMain;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            ivFileType = itemView.findViewById(R.id.ivFileType);
            textViewFileName = itemView.findViewById(R.id.tvFileName);
            textViewFileSize = itemView.findViewById(R.id.tvFileSize);
            buttonDelete = itemView.findViewById(R.id.deleteButton);
            rlMain = itemView.findViewById(R.id.rlMain);

/*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + textViewFileName.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textViewFileName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
*/
        }
    }

    void deleteAttachment(SimpleViewHolder viewHolder,int position,String attachmentId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://full-aureusgroup.cs117.force.com/services/apexrest/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();

        ApiHelper apiHelper=retrofit.create(ApiHelper.class);
        Call<ResponseBody> call =apiHelper.deleteAttachment(attachmentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                    mDataset.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mDataset.size());
                    mItemManger.closeAllItems();
                    Toast.makeText(mContext, "Deleted " + viewHolder.textViewFileName.getText().toString() + "!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    void showDialog(String url) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(com.twilio.video.app.R.layout.dialog_open_file);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes( wlp );
        dialog.getWindow().setLayout( WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT );

        ImageView ivClose = (ImageView) dialog.findViewById(com.twilio.video.app.R.id.ivClose);


        WebView webview = (WebView) dialog.findViewById(com.twilio.video.app.R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.loadUrl(url);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
