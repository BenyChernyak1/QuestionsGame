package com.ebay.game.utils;

public final class Constants {

    private Constants() {
        throw new IllegalStateException();
    }

    public static final int MIN_PLAYERS_NUM = 6;
    public static final int MAX_PLAYERS_NUM = 11;
    public static final int POINTS_FOR_RIGHT_ANSWER = 10;
    public static final int RIGHT_ANSWER_CALC_IN_PERCENT = 75;
    public static final String URL_FOR_QUESTIONS_AND_ANSWERS = "https://opentdb.com/api.php?amount=1&type=multiple";
}
