package com.android.swipe.view.helper.cardStackView;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.swipe.R;
import com.android.swipe.view.helper.cardStackView.internal.CardStackSetting;
import com.android.swipe.view.helper.cardStackView.internal.CardStackSmoothScroller;
import com.android.swipe.view.helper.cardStackView.internal.CardStackState;
import com.android.swipe.view.helper.cardStackView.internal.DisplayUtil;

public class CardStackLayoutManager
        extends RecyclerView.LayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private final Context context;

    private CardStackListener listener = CardStackListener.DEFAULT;
    private CardStackSetting setting = new CardStackSetting();
    private CardStackState state = new CardStackState();

    CardStackLayoutManager(Context context) {
        this(context, CardStackListener.DEFAULT);
    }

    public CardStackLayoutManager(Context context, CardStackListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State s) {
        update(recycler);
    }

    @Override
    public boolean canScrollHorizontally() {
        if (getTopPosition() == getItemCount() - 1) {
            return false;
        } else {
            return setting.swipeableMethod.canSwipe() && setting.canScrollHorizontal;
        }
    }

    @Override
    public boolean canScrollVertically() {
        if (getTopPosition() == getItemCount() - 1) {
            return false;
        } else {
            return setting.swipeableMethod.canSwipe() && setting.canScrollHorizontal;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (state.topPosition == getItemCount()) {
            return 0;
        }

        switch (state.status) {
            case Idle:
            case Dragging:
            case ManualSwipeAnimating:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dx -= dx;
                    update(recycler);
                    return dx;
                }
                break;
            case RewindAnimating:
                state.dx -= dx;
                update(recycler);
                return dx;
            case AutomaticSwipeAnimating:
                if (setting.swipeableMethod.canSwipeAutomatically()) {
                    state.dx -= dx;
                    update(recycler);
                    return dx;
                }
                break;
            case AutomaticSwipeAnimated:
                break;
            case ManualSwipeAnimated:
                break;
        }

        return 0;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (state.topPosition == getItemCount()) {
            return 0;
        }

        switch (state.status) {
            case Idle:
            case Dragging:
            case ManualSwipeAnimating:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.dy -= dy;
                    update(recycler);
                    return dy;
                }
                break;
            case RewindAnimating:
                state.dy -= dy;
                update(recycler);
                return dy;
            case AutomaticSwipeAnimating:
                if (setting.swipeableMethod.canSwipeAutomatically()) {
                    state.dy -= dy;
                    update(recycler);
                    return dy;
                }
                break;
            case AutomaticSwipeAnimated:
                break;
            case ManualSwipeAnimated:
                break;
        }
        return 0;
    }

    @Override
    public void onScrollStateChanged(int s) {
        switch (s) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (state.targetPosition == RecyclerView.NO_POSITION) {
                    state.next(CardStackState.Status.Idle);
                    state.targetPosition = RecyclerView.NO_POSITION;
                } else if (state.topPosition == state.targetPosition) {
                    state.next(CardStackState.Status.Idle);
                    state.targetPosition = RecyclerView.NO_POSITION;
                } else {
                    if (state.topPosition < state.targetPosition) {
                        smoothScrollToNext(state.targetPosition);
                    } else {
                        smoothScrollToPrevious(state.targetPosition);
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (setting.swipeableMethod.canSwipeManually()) {
                    state.next(CardStackState.Status.Dragging);
                }
                break;

            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
        }
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    @Override
    public void scrollToPosition(int position) {
        if (setting.swipeableMethod.canSwipeAutomatically()) {
            if (state.canScrollToPosition(position, getItemCount())) {
                state.topPosition = position;
                requestLayout();
            }
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State s, int position) {
        if (setting.swipeableMethod.canSwipeAutomatically()) {
            if (state.canScrollToPosition(position, getItemCount())) {
                smoothScrollToPosition(position);
            }
        }
    }

    @NonNull
    public CardStackSetting getCardStackSetting() {
        return setting;
    }

    @NonNull
    public CardStackState getCardStackState() {
        return state;
    }

    @NonNull
    public CardStackListener getCardStackListener() {
        return listener;
    }

    void updateProportion(float x, float y) {
        if (getTopPosition() < getItemCount()) {
            View view = findViewByPosition(getTopPosition());
            if (view != null) {
                float half = getHeight() / 2.0f;
                state.proportion = -(y - half - view.getTop()) / half;
            }
        }
    }

    private void update(RecyclerView.Recycler recycler) {
        state.width = getWidth();
        state.height = getHeight();

        if (state.isSwipeCompleted()) {
            removeAndRecycleView(getTopView(), recycler);

            final Direction direction = state.getDirection();

            state.next(state.status.toAnimatedStatus());
            state.topPosition++;
            state.dx = 0;
            state.dy = 0;
            if (state.topPosition == state.targetPosition) {
                state.targetPosition = RecyclerView.NO_POSITION;
            }

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    listener.onCardSwiped(getTopView(), state.topPosition, direction);
                }
            });
        }

        detachAndScrapAttachedViews(recycler);

        final int parentTop = getPaddingTop();
        final int parentLeft = getPaddingLeft();
        final int parentRight = getWidth() - getPaddingLeft();
        final int parentBottom = getHeight() - getPaddingBottom();
        for (int i = state.topPosition; i < state.topPosition + setting.visibleCount && i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            addView(child, 0);
            measureChildWithMargins(child, 0, 0);
            layoutDecoratedWithMargins(child, parentLeft, parentTop, parentRight, parentBottom);

            resetTranslation(child);
            resetScale(child);
            resetRotation(child);
            resetOverlay(child);

            if (i == state.topPosition) {
                updateTranslation(child);
                resetScale(child);
                updateRotation(child);
                updateOverlay(child);
            } else {
                int currentIndex = i - state.topPosition;
                updateTranslation(child, currentIndex);
                updateScale(child, currentIndex);
                resetRotation(child);
                resetOverlay(child);
            }
        }

        if (state.status.isDragging()) {
            listener.onCardDragging(state.getDirection(), state.getRatio());
        }
    }

    private void updateTranslation(View view) {
        view.setTranslationX(state.dx);
        view.setTranslationY(state.dy);
    }

    private void updateTranslation(View view, int index) {
        int nextIndex = index - 1;
        int translationPx = DisplayUtil.dpToPx(context, setting.translationInterval);
        float currentTranslation = index * translationPx;
        float nextTranslation = nextIndex * translationPx;
        float targetTranslation = currentTranslation - (currentTranslation - nextTranslation) * state.getRatio();
        switch (setting.stackFrom) {
            case Left:
                view.setTranslationX(-targetTranslation);
                break;
            case Right:
                view.setTranslationX(targetTranslation);
                break;
        }
    }

    private void resetTranslation(View view) {
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
    }

    private void updateScale(View view, int index) {
        int nextIndex = index - 1;
        float currentScale = 1.0f - index * (1.0f - setting.scaleInterval);
        float nextScale = 1.0f - nextIndex * (1.0f - setting.scaleInterval);
        float targetScale = currentScale + (nextScale - currentScale) * state.getRatio();
        switch (setting.stackFrom) {
            case None:
                view.setScaleX(targetScale);
                view.setScaleY(targetScale);
                break;
            case Top:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case TopAndLeft:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case TopAndRight:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case Bottom:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case BottomAndLeft:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case BottomAndRight:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case Left:
                // TODO Should handle ScaleX
                view.setScaleY(targetScale);
                break;
            case Right:
                // TODO Should handle ScaleX
                view.setScaleY(targetScale);
                break;
        }
    }

    private void resetScale(View view) {
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    private void updateRotation(View view) {
        float degree = state.dx * setting.maxDegree / getWidth() * state.proportion;
        view.setRotation(degree);
    }

    private void resetRotation(View view) {
        view.setRotation(0.0f);
    }

    private void updateOverlay(View view) {
        View leftOverlay = view.findViewById(R.id.left_overlay);
        if (leftOverlay != null) {
            leftOverlay.setAlpha(0.0f);
        }
        View rightOverlay = view.findViewById(R.id.right_overlay);
        if (rightOverlay != null) {
            rightOverlay.setAlpha(0.0f);
        }
        View topOverlay = view.findViewById(R.id.top_overlay);
        if (topOverlay != null) {
            topOverlay.setAlpha(0.0f);
        }
        View bottomOverlay = view.findViewById(R.id.bottom_overlay);
        if (bottomOverlay != null) {
            bottomOverlay.setAlpha(0.0f);
        }
        Direction direction = state.getDirection();
        float alpha = setting.overlayInterpolator.getInterpolation(state.getRatio());
        switch (direction) {
            case Left:
                if (leftOverlay != null) {
                    leftOverlay.setAlpha(alpha);
                }
                break;
            case Right:
                if (rightOverlay != null) {
                    rightOverlay.setAlpha(alpha);
                }
                break;
            case Top:
                if (topOverlay != null) {
                    topOverlay.setAlpha(alpha);
                }
                break;
            case Bottom:
                if (bottomOverlay != null) {
                    bottomOverlay.setAlpha(alpha);
                }
                break;
        }
    }

    private void resetOverlay(View view) {
        View leftOverlay = view.findViewById(R.id.left_overlay);
        if (leftOverlay != null) {
            leftOverlay.setAlpha(0.0f);
        }
        View rightOverlay = view.findViewById(R.id.right_overlay);
        if (rightOverlay != null) {
            rightOverlay.setAlpha(0.0f);
        }
        View topOverlay = view.findViewById(R.id.top_overlay);
        if (topOverlay != null) {
            topOverlay.setAlpha(0.0f);
        }
        View bottomOverlay = view.findViewById(R.id.bottom_overlay);
        if (bottomOverlay != null) {
            bottomOverlay.setAlpha(0.0f);
        }
    }

    private void smoothScrollToPosition(int position) {
        if (state.topPosition < position) {
            smoothScrollToNext(position);
        } else {
            smoothScrollToPrevious(position);
        }
    }

    private void smoothScrollToNext(int position) {
        state.proportion = 0.0f;
        state.targetPosition = position;
        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticSwipe, this);
        scroller.setTargetPosition(state.topPosition);
        startSmoothScroll(scroller);
    }

    private void smoothScrollToPrevious(int position) {

        state.proportion = 0.0f;
        state.targetPosition = position;
        state.topPosition--;
        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticRewind, this);
        scroller.setTargetPosition(state.topPosition);
        startSmoothScroll(scroller);
    }

    public View getTopView() {
        return findViewByPosition(state.topPosition);
    }

    public int getTopPosition() {
        return state.topPosition;
    }

    public void setTopPosition(int topPosition) {
        state.topPosition = topPosition;
    }


    public void setSwipeableMethod(SwipeableMethod swipeableMethod) {
        setting.swipeableMethod = swipeableMethod;
    }

    public void setSwipeAnimationSetting(@NonNull SwipeAnimationSetting swipeAnimationSetting) {
        setting.swipeAnimationSetting = swipeAnimationSetting;
    }


    public void setOverlayInterpolator(@NonNull Interpolator overlayInterpolator) {
        setting.overlayInterpolator = overlayInterpolator;
    }

}
