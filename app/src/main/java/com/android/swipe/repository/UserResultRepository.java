package com.android.swipe.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.android.swipe.R;
import com.android.swipe.adapter.UserListAdapter;
import com.android.swipe.database.AppDatabase;
import com.android.swipe.database.UserResponseDao;
import com.android.swipe.model.Result;
import com.android.swipe.model.User;
import com.android.swipe.network.ApiEndPoint;
import com.android.swipe.network.Resource;
import com.android.swipe.network.RetrofitService;
import com.android.swipe.network.response.ApiResponse;
import com.android.swipe.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserResultRepository {
    private Context mContext;
    private ApiEndPoint mApiEndPoint;
    private List<User> mResultList;
    private MediatorLiveData<Resource<List<User>>> mResponseLiveData;
    private int mLastListSize;
    private ExecutorService mExecutorService;
    private UserResponseDao mUserResponseDao;

    public UserResultRepository(Context context) {
        mContext = context;
        mApiEndPoint = RetrofitService.getRetrofitInstance().create(ApiEndPoint.class);
        mResponseLiveData = new MediatorLiveData<>();
        mResultList = new ArrayList<>();
        mExecutorService = Executors.newSingleThreadExecutor();
        mUserResponseDao = AppDatabase.getInstance(mContext).responseDao();
    }


    public LiveData<Resource<List<User>>> getResult() {
        if (Util.isNetworkConnected(mContext)) {
            if (mResultList.isEmpty()) {
                mResponseLiveData.setValue(Resource.loading());
            }
            LiveData<ApiResponse<Result>> apiSource = mApiEndPoint.getUserInfo();
            mResponseLiveData.addSource(apiSource,
                    resultApiResponse -> {
                        mResponseLiveData.removeSource(apiSource);
                        if (resultApiResponse.getData() != null) {
                            prePareList(resultApiResponse.getData());
                        } else {
                            mResponseLiveData.setValue(Resource.error(resultApiResponse.getError()));
                        }
                    });
        } else {
            LiveData<List<User>> dataBaseSource = mUserResponseDao.getAllResponse();
            mResponseLiveData.addSource(dataBaseSource, list -> {
                removeLoader();
                mResponseLiveData.removeSource(dataBaseSource);
                if (!mResultList.isEmpty()) {
                    if (list != null && !list.isEmpty()) {
                        mResultList.addAll(list);
                    }
                    mResultList.add(getNoDataEmptyFooter(UserListAdapter.VIEW_TYPE_NO_DATA));
                    mResponseLiveData.setValue(Resource.success(mResultList, mLastListSize));
                } else {
                    mResponseLiveData.setValue(Resource.error(mContext.getString(R.string.no_favorite_no_network_msg)));
                }
            });
        }

        return mResponseLiveData;
    }

    private User getNoDataEmptyFooter(int viewTypeNoData) {
        User empty = new User();
        empty.setViewType(viewTypeNoData);
        return empty;
    }

    private void prePareList(Result data) {
        User item = data.getResults().get(0).getUser();

        removeLoader();
        mResultList.add(item);

        mResultList.add(getNoDataEmptyFooter(UserListAdapter.VIEW_TYPE_LOAD_NEXT));
        mResponseLiveData.setValue(Resource.success(mResultList, mLastListSize));
        mLastListSize = mResultList.size() - 1;

    }

    private void removeLoader() {
        if (mResultList != null && !mResultList.isEmpty()) {
            int viewType = mResultList.get(mResultList.size() - 1).getViewType();
            if ((viewType == UserListAdapter.VIEW_TYPE_LOAD_NEXT ||
                    viewType == UserListAdapter.VIEW_TYPE_NO_DATA)) {
                mResultList.remove(mResultList.size() - 1);
            }
        }
    }


    public void addToDb(final int position, boolean isSave) {
        mExecutorService.execute(() -> {
            if (isSave) {
                mUserResponseDao.insertFavorite(mResultList.get(position));
            } else {
                mUserResponseDao.deleteFavorite(mResultList.get(position).getKey());
            }
        });

    }

    public void clear() {
        mExecutorService.shutdown();
    }
}
