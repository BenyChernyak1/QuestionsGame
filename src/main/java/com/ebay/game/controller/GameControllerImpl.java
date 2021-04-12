package com.ebay.game.controller;

import com.ebay.game.model.api.AnswerQuestionResponse;
import com.ebay.game.model.api.GetLeaderBoardResponse;
import com.ebay.game.model.api.GetQuestionResponse;
import com.ebay.game.model.queue.AnswerMessage;
import com.ebay.game.model.queue.QuestionMessage;
import com.ebay.game.service.GameService;
import com.google.common.base.Preconditions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("game")
public class GameControllerImpl implements GameController{

    @Autowired
    private GameService gameService;

    @Value("${service.rabbitmq.exchange.name}")
    private String topicExchangeName;

    @Value("${service.rabbitmq.questions.routing.key}")
    private String questionsRoutingKey;

    @Value("${service.rabbitmq.answers.routing.key}")
    private String answersRoutingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @RequestMapping(value = "/question", method = RequestMethod.GET)
    public ResponseEntity<GetQuestionResponse> getQuestion(String username, int gameId) {
        try {
            Preconditions.checkArgument(username != null);
            Preconditions.checkArgument(gameId > 0);

            //create question message
            QuestionMessage questionMessage = new QuestionMessage(username, gameId);
            GetQuestionResponse getQuestionResponse = (GetQuestionResponse)rabbitTemplate.convertSendAndReceive(topicExchangeName, questionsRoutingKey, questionMessage);

            return new ResponseEntity<>(getQuestionResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            GetQuestionResponse getQuestionResponse = new GetQuestionResponse(0, null, null, null);
            getQuestionResponse.setErrorMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(getQuestionResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @RequestMapping(value = "/answer", method = RequestMethod.GET)
    public ResponseEntity<AnswerQuestionResponse> answerQuestion(String username, int gameId, int answerId, String questionId) {
        try {
            Preconditions.checkArgument(username != null);
            Preconditions.checkArgument(gameId > 0);
            Preconditions.checkArgument(answerId >= 1 && answerId < 5);
            Preconditions.checkArgument(questionId != null);

            //create answers message
            AnswerMessage answerMessage = new AnswerMessage(username, gameId, answerId, questionId);
            AnswerQuestionResponse answerQuestionResponse = (AnswerQuestionResponse)rabbitTemplate.convertSendAndReceive(topicExchangeName, answersRoutingKey, answerMessage);

            return new ResponseEntity<>(answerQuestionResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AnswerQuestionResponse answerQuestionResponse = new AnswerQuestionResponse(null, 0);
            answerQuestionResponse.setErrorMessage(e.getLocalizedMessage());
            return new ResponseEntity<>(answerQuestionResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
    public ResponseEntity<GetLeaderBoardResponse> getLeaderboard(int gameId) {
        try {
            Preconditions.checkArgument(gameId > 0);
            GetLeaderBoardResponse getLeaderBoardResponse = gameService.getLeaderboard(gameId);
            return new ResponseEntity<>(getLeaderBoardResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            GetLeaderBoardResponse getLeaderBoardResponse = getGetLeaderBoardErrorResponse(e.getLocalizedMessage());
            return new ResponseEntity<>(getLeaderBoardResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            GetLeaderBoardResponse getLeaderBoardResponse = getGetLeaderBoardErrorResponse(e.getLocalizedMessage());
            e.printStackTrace();
            return new ResponseEntity<>(getLeaderBoardResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private GetLeaderBoardResponse getGetLeaderBoardErrorResponse(String localizedMessage) {
        GetLeaderBoardResponse getLeaderBoardResponse = new GetLeaderBoardResponse(null);
        getLeaderBoardResponse.setErrorMessage(localizedMessage);
        return getLeaderBoardResponse;
    }
}
