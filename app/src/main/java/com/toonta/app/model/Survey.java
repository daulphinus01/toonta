/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;


import com.toonta.app.ToontaDAO;

import java.io.Serializable;
import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [06/06/2016]
 */
public class Survey implements Serializable {

    private String surveyId;
    private String name;
    private String summary;
    private List<ToontaDAO.QuestionsList.Question> questions;
    private Long reward;
    private String title;
    private String plusIcon;

    public Survey() {
    }

    public Survey (String title, String plusIcon) {
        this.title = title;
        this.plusIcon = plusIcon;
    }

    public String getTitle() {
        return title;
    }

    public String getPlusIcon() {
        return plusIcon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ToontaDAO.QuestionsList.Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ToontaDAO.QuestionsList.Question> questions) {
        this.questions = questions;
    }

    public Long getReward() {
        return reward;
    }

    public void setReward(Long reward) {
        this.reward = reward;
    }

    public void setPlusIcon(String plusIcon) {
        this.plusIcon = plusIcon;
    }
}
