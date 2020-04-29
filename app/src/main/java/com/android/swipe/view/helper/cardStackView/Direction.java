package com.android.swipe.view.helper.cardStackView;

import java.util.Arrays;
import java.util.List;

public enum Direction {
    Left,
    Right,
    Top,
    Bottom;

    public static final List<Direction> HORIZONTAL = Arrays.asList(Direction.Left, Direction.Right);
}
