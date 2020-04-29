package com.android.swipe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.swipe.R;
import com.android.swipe.model.User;
import com.android.swipe.utils.IconSelectedType;
import com.android.swipe.utils.NextLoadListener;
import com.bumptech.glide.Glide;

import java.util.List;

import static com.android.swipe.utils.IconSelectedType.call;
import static com.android.swipe.utils.IconSelectedType.dob;
import static com.android.swipe.utils.IconSelectedType.location;
import static com.android.swipe.utils.IconSelectedType.name;
import static com.android.swipe.utils.IconSelectedType.ssl;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    public static final int VIEW_TYPE_LOAD_NEXT = 1;
    public static final int VIEW_TYPE_NO_DATA = 2;
    private static final int VIEW_TYPE_LIST_ITEM = 0;
    private List<User> mList;
    private Context mContext;
    private int mLastLoadPosition;
    private int mSelectedColor;
    private int mUnSelectedColor;
    private NextLoadListener mListener;

    public UserListAdapter(Context context, List<User> list, NextLoadListener loadListener) {
        mList = list;
        mContext = context;
        mSelectedColor = ContextCompat.getColor(mContext, R.color.colorAccent);
        mUnSelectedColor = ContextCompat.getColor(mContext, R.color.colorSilver);
        mListener = loadListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_NEXT || viewType == VIEW_TYPE_NO_DATA) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.load_more, parent, false), viewType);
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_list_item, parent, false), viewType);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User item = mList.get(position);
        if (item.getViewType() == VIEW_TYPE_LIST_ITEM) {

            Glide.with(mContext).load(item.getPicture())
                    .into(holder.imageView);

            holder.accountIcon.setColorFilter(mUnSelectedColor);
            holder.mapIcon.setColorFilter(mUnSelectedColor);
            holder.mailIcon.setColorFilter(mUnSelectedColor);
            holder.callIcon.setColorFilter(mUnSelectedColor);
            holder.dobIcon.setColorFilter(mUnSelectedColor);

            setSelectedText(item, holder);

            holder.dobIcon.setTag(position);
            holder.mapIcon.setTag(position);
            holder.accountIcon.setTag(position);
            holder.mailIcon.setTag(position);
            holder.callIcon.setTag(position);

        } else if (item.getViewType() == VIEW_TYPE_LOAD_NEXT && mLastLoadPosition != position) {
            mLastLoadPosition = position;
            mListener.onNextLoad();
            holder.errorMessage.setText(R.string.loading);
            holder.tryButton.setVisibility(View.GONE);
        } else {
            holder.errorMessage.setText(R.string.no_favorite_no_network_msg);
            holder.tryButton.setVisibility(View.VISIBLE);

        }
    }

    private void setSelectedText(User item, ViewHolder holder) {
        String title = null;
        String subtitle = null;

        switch (item.getIconSelectedType()) {
            case name:
                title = mContext.getString(R.string.my_name_is);
                subtitle = item.getName().getFirst() + " " + item.getName().getLast();
                holder.accountIcon.setColorFilter(mSelectedColor);
                break;
            case dob:
                title = mContext.getString(R.string.my_dob_is);
                subtitle = item.getDob();
                holder.dobIcon.setColorFilter(mSelectedColor);
                break;
            case location:
                title = mContext.getString(R.string.my_address_is);
                subtitle = item.getLocation().getStreet() + " "
                        + item.getLocation().getCity() + " " +
                        item.getLocation().getState();
                holder.mapIcon.setColorFilter(mSelectedColor);
                break;
            case call:
                title = mContext.getString(R.string.my_phone_number_is);
                subtitle = item.getPhone();
                holder.callIcon.setColorFilter(mSelectedColor);
                break;
            case ssl:
                title = mContext.getString(R.string.my_email_is);
                subtitle = item.getEmail();
                holder.mailIcon.setColorFilter(mSelectedColor);
                break;
        }
        holder.title.setText(title);
        holder.subTitle.setText(subtitle);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void update(List<User> data, int startPosition) {
        mList = data;
        notifyItemRangeChanged(startPosition, data.size());
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position)
                .getViewType();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView subTitle;
        private TextView title;
        private ImageView accountIcon;
        private ImageView mapIcon;
        private ImageView callIcon;
        private ImageView dobIcon;
        private ImageView mailIcon;
        private TextView errorMessage;
        private Button tryButton;


        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_LIST_ITEM) {
                imageView = itemView.findViewById(R.id.image_view);
                subTitle = itemView.findViewById(R.id.sub_title);
                title = itemView.findViewById(R.id.title);

                accountIcon = itemView.findViewById(R.id.account_icon);
                mapIcon = itemView.findViewById(R.id.map_icon);
                dobIcon = itemView.findViewById(R.id.dob_icon);
                mailIcon = itemView.findViewById(R.id.mail_icon);
                callIcon = itemView.findViewById(R.id.call_icon);

                callIcon.setOnClickListener(this);
                mailIcon.setOnClickListener(this);
                accountIcon.setOnClickListener(this);
                mapIcon.setOnClickListener(this);
                dobIcon.setOnClickListener(this);
            } else {
                errorMessage = itemView.findViewById(R.id.error_message);
                tryButton = itemView.findViewById(R.id.retry);
                tryButton.setOnClickListener(this);
            }
        }


        @Override
        public void onClick(View v) {
            IconSelectedType type = name;
            switch (v.getId()) {
                case R.id.account_icon:
                    type = name;
                    break;
                case R.id.call_icon:
                    type = call;
                    break;
                case R.id.map_icon:
                    type = location;
                    break;
                case R.id.dob_icon:
                    type = dob;
                    break;
                case R.id.mail_icon:
                    type = ssl;
                    break;
                case R.id.retry:
                    mListener.onNextLoad();
                    return;
            }
            int position = (int) v.getTag();
            mList.get(position).setIconSelectedType(type);
            notifyItemChanged(position);
        }
    }

}

