package com.android.swipe.view.helper.cardStackView;

import android.view.View;

public interface CardStackListener {
    void onCardDragging(Direction direction, float ratio);

    void onCardSwiped(View view, int position, Direction direction);

    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {

        }

        @Override
        public void onCardSwiped(View view, int position, Direction direction) {

        }
    };
}
