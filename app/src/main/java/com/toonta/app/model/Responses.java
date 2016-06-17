/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;/**
 * Created by Kevin on 15/06/2016.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [15/06/2016]
 */
public class Responses {
    public class Response {
        public String choiceId;
        public String questionId;
        public String textAnswer;
        public String yesNo;

        @Override
        public String toString() {
            return "Response{" +
                    "choiceId='" + choiceId + '\'' +
                    ", questionId='" + questionId + '\'' +
                    ", textAnswer='" + textAnswer + '\'' +
                    ", yesNo='" + yesNo + '\'' +
                    '}';
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
