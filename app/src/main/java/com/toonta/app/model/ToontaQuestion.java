/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [08/06/2016]
 */
public class ToontaQuestion implements Parcelable{
    private String question;
    private QuestionType questionType;
    private List<String> responses;

    public ToontaQuestion() {
    }

    public ToontaQuestion(String question, QuestionType questionType, List<String> responses) {
        this.question = question;
        this.questionType = questionType;
        this.responses = responses;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ToontaQuestion(Parcel in) {
        question = in.readString();
        questionType = QuestionType.valueOf(in.readString());
        in.readStringList(responses);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(questionType.name());
        dest.writeStringList(responses);
    }

    public static final Creator<ToontaQuestion> CREATOR = new Creator<ToontaQuestion>() {
        @Override
        public ToontaQuestion createFromParcel(Parcel in) {
            return new ToontaQuestion(in);
        }

        @Override
        public ToontaQuestion[] newArray(int size) {
            return new ToontaQuestion[size];
        }
    };
}
