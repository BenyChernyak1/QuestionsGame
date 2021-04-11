package com.ebay.game.model.api.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExternalQuestionMessage {

    private int response_code;

    private List<Result> results;

    public ExternalQuestionMessage(@JsonProperty("response_code") int response_code, @JsonProperty("results") List<Result> results) {
        this.response_code = response_code;
        this.results = results;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
