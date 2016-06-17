/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [12/06/2016]
 */
public class SurveyResponse implements Parcelable {
    public SurveyResponse(Parcel in) {
        respondentId = in.readString();
        surveyId = in.readString();
        responses = in.createTypedArrayList(AtomicResponseRequest.CREATOR);
    }

    public static final Creator<SurveyResponse> CREATOR = new Creator<SurveyResponse>() {
        @Override
        public SurveyResponse createFromParcel(Parcel in) {
            return new SurveyResponse(in);
        }

        @Override
        public SurveyResponse[] newArray(int size) {
            return new SurveyResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(respondentId);
        dest.writeString(surveyId);
        dest.writeTypedList(responses);
    }

    public static class AtomicResponseRequest implements Parcelable{
        public String choiceId;
        public String questionId;
        public String textAnswer;
        public String yesNoAnswer;

        public AtomicResponseRequest() {}

        protected AtomicResponseRequest(Parcel in) {
            choiceId = in.readString();
            questionId = in.readString();
            textAnswer = in.readString();
            yesNoAnswer = in.readString();
        }

        public static final Creator<AtomicResponseRequest> CREATOR = new Creator<AtomicResponseRequest>() {
            @Override
            public AtomicResponseRequest createFromParcel(Parcel in) {
                return new AtomicResponseRequest(in);
            }

            @Override
            public AtomicResponseRequest[] newArray(int size) {
                return new AtomicResponseRequest[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(choiceId);
            dest.writeString(questionId);
            dest.writeString(textAnswer);
            dest.writeString(yesNoAnswer);
        }
    }

    public String respondentId;
    public String surveyId;
    public List<AtomicResponseRequest> responses;

    public SurveyResponse(){}
}
