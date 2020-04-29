package com.android.swipe.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.android.swipe.R;
import com.android.swipe.adapter.UserListAdapter;
import com.android.swipe.model.User;
import com.android.swipe.network.Resource;
import com.android.swipe.view.helper.cardStackView.CardStackLayoutManager;
import com.android.swipe.view.helper.cardStackView.CardStackListener;
import com.android.swipe.view.helper.cardStackView.CardStackView;
import com.android.swipe.view.helper.cardStackView.Direction;
import com.android.swipe.view.helper.cardStackView.SwipeableMethod;
import com.android.swipe.viewModel.ResultViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStackListener, Observer<Resource<List<User>>> {

    private CardStackView mCardStackView;
    private CardStackLayoutManager mCardStackLayoutManager;
    private ResultViewModel mViewModel;
    private UserListAdapter mListAdapter;
    private TextView mErrorText;
    private Button mTryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(ResultViewModel.class);
        mViewModel.getResult();
        mViewModel.getResultLiveData().observe(this, this);
        initView();
    }

    private void initView() {
        mCardStackView = findViewById(R.id.recyclerView);
        mErrorText = findViewById(R.id.error_message);
        mTryButton = findViewById(R.id.retry);
        mTryButton.setOnClickListener(v -> loadNext());
        mCardStackLayoutManager = new CardStackLayoutManager(getApplicationContext(), this);
        initStackView();
        mCardStackView.setLayoutManager(mCardStackLayoutManager);

    }

    private void initStackView() {
        mCardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual);
        mCardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        RecyclerView.ItemAnimator animator = mCardStackView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(View view, int position, Direction direction) {
        mViewModel.addTodb(position - 1, direction == Direction.Right);
    }


    @Override
    public void onChanged(Resource<List<User>> listResource) {
        mTryButton.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
        switch (listResource.status) {
            case ERROR:
            case NETWORK_ERROR:
                mErrorText.setText(listResource.getMessage());
                mTryButton.setVisibility(View.VISIBLE);
                mErrorText.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                if (mListAdapter != null) {
                    mListAdapter.update(listResource.data, listResource.getUpdatePosition());
                } else {
                    mListAdapter = new UserListAdapter(getApplicationContext(), listResource.data, this::loadNext);
                    mCardStackView.setAdapter(mListAdapter);
                }
                break;
            case LOADING:
                mErrorText.setText(R.string.loading);
                mErrorText.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void loadNext() {
        mViewModel.getResult();
    }
}
