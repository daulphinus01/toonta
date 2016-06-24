/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;/**
 * Created by Kevin on 15/06/2016.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [15/06/2016]
 */
public class Responses implements Parcelable {
    public Responses () {}

    protected Responses(Parcel in) {
        respondentId = in.readString();
        surveyId = in.readString();
        responses = in.createTypedArrayList(Response.CREATOR);
    }

    public static final Creator<Responses> CREATOR = new Creator<Responses>() {
        @Override
        public Responses createFromParcel(Parcel in) {
            return new Responses(in);
        }

        @Override
        public Responses[] newArray(int size) {
            return new Responses[size];
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

    public static class Response implements Parcelable {
        public String choiceId;
        public String questionId;
        public String textAnswer;
        public String yesNo;

        protected Response(Parcel in) {
            choiceId = in.readString();
            questionId = in.readString();
            textAnswer = in.readString();
            yesNo = in.readString();
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };

        public Response() {}

        @Override
        public String toString() {
            return "Response{" +
                    "choiceId='" + choiceId + '\'' +
                    ", questionId='" + questionId + '\'' +
                    ", textAnswer='" + textAnswer + '\'' +
                    ", yesNo='" + yesNo + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(choiceId);
            dest.writeString(questionId);
            dest.writeString(textAnswer);
            dest.writeString(yesNo);
        }
    }

    public String respondentId;
    public  String surveyId;
    public List<Response> responses = new ArrayList<>();

    public Response createResponse() {
        return new Response();
    }

    @Override
    public String toString() {
        return "Responses{" +
                "respondentId='" + respondentId + '\'' +
                ", surveyId='" + surveyId + '\'' +
                ", responses=" + responses +
                '}';
    }
}
