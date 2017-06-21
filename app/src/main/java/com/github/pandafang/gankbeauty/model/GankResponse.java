package com.github.pandafang.gankbeauty.model;

import java.util.List;

/**
 * Created by panda on 2017/6/21.
 */

public class GankResponse {

    private Boolean error;

    private List<Girl> results;


    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Girl> getResults() {
        return results;
    }

    public void setResults(List<Girl> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "Respon{" +
                "error=" + error +
                ", results=" + results +
                '}';
    }
}
