package com.ebay.game.controller;

import com.ebay.game.model.api.AnswerQuestionResponse;
import com.ebay.game.model.api.GetLeaderBoardResponse;
import com.ebay.game.model.api.GetQuestionResponse;
import org.springframework.http.ResponseEntity;

public interface GameController {

    ResponseEntity<GetQuestionResponse> getQuestion(String username, int gameId);

    ResponseEntity<AnswerQuestionResponse> answerQuestion(String username, int gameId, int answerId, String questionId);

    ResponseEntity<GetLeaderBoardResponse> getLeaderboard(int gameId);
}
