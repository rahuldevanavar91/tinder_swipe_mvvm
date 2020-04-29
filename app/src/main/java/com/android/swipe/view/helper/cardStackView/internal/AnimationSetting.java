package com.android.swipe.view.helper.cardStackView.internal;

import android.view.animation.Interpolator;

import com.android.swipe.view.helper.cardStackView.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
