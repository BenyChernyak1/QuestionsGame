package com.ebay.game.service;

import com.ebay.game.model.Game;
import com.ebay.game.model.Player;
import com.ebay.game.model.Question;
import com.ebay.game.model.Status;
import com.ebay.game.model.api.AnswerQuestionResponse;
import com.ebay.game.model.api.GetLeaderBoardResponse;
import com.ebay.game.model.api.GetQuestionResponse;
import com.ebay.game.model.api.external.ExternalQuestionMessage;
import com.ebay.game.model.queue.AnswerMessage;
import com.ebay.game.model.queue.QuestionMessage;
import com.ebay.game.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class GameServiceImpl implements GameService {

    private List<Game> games = new ArrayList<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public GetQuestionResponse getQuestion(Message message) throws Exception {
        QuestionMessage questionMessage = parseQuestionMessage(message);

        final GetQuestionResponse[] getQuestionResponse = {null};

        Player newPlayer = new Player(questionMessage.getUsername());

        if (games.stream().anyMatch(game -> game.getId() == questionMessage.getGameId())) {
            games.stream().filter(game -> game.getId() == questionMessage.getGameId()).forEach(
                game -> {
//                    game.getPlayers().add(newPlayer);
                    getQuestionResponse[0] = new GetQuestionResponse(game.getId(), game.getQuestion().getId(), game.getQuestion().getText(), new ArrayList<>(game.getQuestion().getAnswers().values()));
                }
            );

            return getQuestionResponse[0];
        } else {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.URL_FOR_QUESTIONS_AND_ANSWERS)
                    .build();

            Response response = client.newCall(request).execute();

            ExternalQuestionMessage externalQuestionMessage = mapper.readValue(response.body().byteStream(), ExternalQuestionMessage.class);

            List<String> possibleAnswers = externalQuestionMessage.getResults().get(0).getIncorrect_answers();
            possibleAnswers.add(externalQuestionMessage.getResults().get(0).getCorrect_answer());
            Map<Integer, String> internalAnswers = IntStream
                    .range(0, possibleAnswers.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> i, possibleAnswers::get));
            Question question = new Question(
                    UUID.randomUUID(),
                    externalQuestionMessage.getResults().get(0).getQuestion(),
                    internalAnswers);
            Game game = new Game(questionMessage.getGameId(), question);
            games.add(game);

            return new GetQuestionResponse(game.getId(), game.getQuestion().getId(), game.getQuestion().getText(), new ArrayList<>(game.getQuestion().getAnswers().values()));
        }
    }

    @Override
    public AnswerQuestionResponse answerQuestion(Message message) throws Exception {
        AnswerMessage answerMessage = parseAnswerMessage(message);
        AnswerQuestionResponse answerQuestionResponse = new AnswerQuestionResponse();

        if (games.stream().noneMatch(game -> game.getId() == answerMessage.getGameId())) {
            answerQuestionResponse.setErrorMessage("No game with ID " + answerMessage.getGameId() + " found");
        }

        if (games.stream().noneMatch(game -> game.getQuestion().getId().toString().equalsIgnoreCase(answerMessage.getQuestionId()))) {
            answerQuestionResponse.setErrorMessage("No question with ID " + answerMessage.getQuestionId() + " found");
        }

        games.stream().filter(game -> game.getId() == answerMessage.getGameId()).forEach(
                game -> {
                    Question question = game.getQuestion();
                    if(question.getCurrentPlayersNumber() == Constants.MAX_PLAYERS_NUM) {
                        answerQuestionResponse.setErrorMessage("Maximum players limit exceeded");
                    }
                }
        );

        if (answerQuestionResponse.getErrorMessage() != null) {
            return answerQuestionResponse;
        }

        Player newPlayer = new Player(answerMessage.getUsername());

        if (games.stream()
                .filter(game -> game.getId() == answerMessage.getGameId())
                .flatMap(game -> game.getPlayers().stream())
                .filter(player -> player.getUsername().equalsIgnoreCase(newPlayer.getUsername()))
                .anyMatch(player ->
                        player.getPointsPerQuestion().getOrDefault(UUID.fromString(answerMessage.getQuestionId()), -1) != -1)) {
            answerQuestionResponse.setErrorMessage("You have already answer this question with ID " + answerMessage.getQuestionId());
            return answerQuestionResponse;
        }

        newPlayer.setPointsPerQuestion(new HashMap<UUID, Integer>(){{put(UUID.fromString(answerMessage.getQuestionId()), 0);}});
        games.stream().filter(game -> game.getId() == answerMessage.getGameId()).forEach(
                game -> {
                    Question question = game.getQuestion();
                    question.getPlayersAnswers().put(newPlayer.getId(), answerMessage.getAnswerId());
                    game.getPlayers().add(newPlayer);
                    question.incrementPlayersNumber();
                    if(question.getCurrentPlayersNumber() >= Constants.MIN_PLAYERS_NUM) {
                        int answerFrequency = Collections.frequency(question.getPlayersAnswers().values(), answerMessage.getAnswerId());
                        if(answerFrequency*100/question.getCurrentPlayersNumber() >= Constants.RIGHT_ANSWER_CALC_IN_PERCENT) {
                            question.setStatus(Status.Resolved);
                            question.getPlayersAnswers().entrySet().stream().filter(uuidIntegerEntry -> uuidIntegerEntry.getValue() == answerMessage.getAnswerId()).forEach(
                                    uuidIntegerEntry -> game.getPlayers().stream().filter(player -> player.getId().equals(uuidIntegerEntry.getKey())).forEach(
                                            player -> {
                                                player.addPointsPerQuestion(question.getId());
                                                if (game.getLeaderboard().containsKey(player.getUsername())) {
                                                    game.getLeaderboard().replace(player.getUsername(), Constants.POINTS_FOR_RIGHT_ANSWER);
                                                } else {
                                                    game.getLeaderboard().put(answerMessage.getUsername(), Constants.POINTS_FOR_RIGHT_ANSWER);
                                                }
                                                if (newPlayer.getId().equals(player.getId())) {
                                                    answerQuestionResponse.setPoints(Constants.POINTS_FOR_RIGHT_ANSWER);
                                                    answerQuestionResponse.setStatus(question.getStatus());
                                                }
                                            }
                                    )
                            );

                        } else {
                            game.getLeaderboard().put(answerMessage.getUsername(), 0);
                            if(!question.getStatus().equals(Status.Resolved)) {
                                question.setStatus(Status.Unresolved);
                            }
                            answerQuestionResponse.setPoints(0);
                            answerQuestionResponse.setStatus(question.getStatus());
                        }
                    } else {
                        game.getLeaderboard().put(answerMessage.getUsername(), 0);
                        answerQuestionResponse.setPoints(0);
                        answerQuestionResponse.setStatus(question.getStatus());
                    }
                }
        );

        return answerQuestionResponse;
    }

    @Override
    public GetLeaderBoardResponse getLeaderboard(int gameId) throws Exception {
        GetLeaderBoardResponse getLeaderBoardResponse = new GetLeaderBoardResponse();
        if (games.stream().noneMatch(game -> game.getId() == gameId)) {
            getLeaderBoardResponse.setErrorMessage("No game with ID " + gameId + " found");
            return getLeaderBoardResponse;
        }

        Map<String, Integer> leaderboard = Objects.requireNonNull(games.stream().filter(game -> game.getId() == gameId).findFirst().orElse(null)).getLeaderboard();
        getLeaderBoardResponse.setLeaderBoard(leaderboard);
        return getLeaderBoardResponse;
    }

    private QuestionMessage parseQuestionMessage(Message message) {
        String messageString = new String(message.getBody());

        // Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder();
        manageDateParsing(builder);

        Gson gson = builder.create();

        return gson.fromJson(messageString, QuestionMessage.class);
    }

    private AnswerMessage parseAnswerMessage(Message message) {
        String messageString = new String(message.getBody());

        // Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder();

        // Register an adapter to manage the date types as long values
        manageDateParsing(builder);

        Gson gson = builder.create();

        return gson.fromJson(messageString, AnswerMessage.class);
    }

    private void manageDateParsing(GsonBuilder builder) {
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
    }
}
