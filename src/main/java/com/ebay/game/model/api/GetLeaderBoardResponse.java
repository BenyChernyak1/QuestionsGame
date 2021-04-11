package com.ebay.game.model.api;

import java.util.HashMap;
import java.util.Map;

public class GetLeaderBoardResponse extends BaseResponse{

    private Map<String, Integer> leaderBoard;

    /** Default constructor. */
    public GetLeaderBoardResponse() {
        super();
    }

    public GetLeaderBoardResponse(Map<String, Integer> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public Map<String, Integer> getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(Map<String, Integer> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    @Override
    public String toString() {
        return "GetLeaderBoardResponse{" +
                "leaderBoard=" + leaderBoard +
                '}';
    }
}
