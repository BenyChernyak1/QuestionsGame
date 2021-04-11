package com.ebay.game.service;

import com.ebay.game.model.Question;
import com.ebay.game.model.api.AnswerQuestionResponse;
import com.ebay.game.model.api.GetLeaderBoardResponse;
import com.ebay.game.model.api.GetQuestionResponse;
import org.springframework.amqp.core.Message;

import java.util.Map;

public interface GameService {

    GetQuestionResponse getQuestion(Message message) throws Exception;

    AnswerQuestionResponse answerQuestion(Message message) throws Exception;

    GetLeaderBoardResponse getLeaderboard(int gameId) throws Exception;
}
