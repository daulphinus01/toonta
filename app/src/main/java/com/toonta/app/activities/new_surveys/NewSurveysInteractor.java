package com.toonta.app.activities.new_surveys;

import android.content.Context;


import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.model.SurveyResponse;

import java.util.ArrayList;

/**
 * Created by Guillaume on 22/05/2016.
 */
public class NewSurveysInteractor {

    public interface NewSurveysViewUpdater {
        void onNewSurveys(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList, boolean reset);
        void onPopulateSurvies(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList);
        void onRefreshProgress();
        void onRefreshDone();
        void onFailure(String error);
    }

    public interface OneSurveyViewUpdator {
        void onGetSurvey(ToontaDAO.QuestionsList QuestionsList);
        void onPostResponse(String statusCode);
        void onFailure(String error);
    }

    NewSurveysViewUpdater newSurveysViewUpdater;
    OneSurveyViewUpdator oneSurveyViewUpdator;
    Context context;
    int currentPage = 1;

    public NewSurveysInteractor(Context context, NewSurveysViewUpdater newSurveysViewUpdater) {
        this.newSurveysViewUpdater = newSurveysViewUpdater;
        this.context = context;
        this.newSurveysViewUpdater.onRefreshProgress();
    }

    public NewSurveysInteractor(Context context, OneSurveyViewUpdator oneSurveyViewUpdator) {
        this.context = context;
        this.oneSurveyViewUpdator = oneSurveyViewUpdator;
    }

    //Used by activity

    public void submitRefreshList() {
        newSurveysViewUpdater.onRefreshProgress();
        currentPage = 1;
        ToontaDAO.getSurveys(currentPage, new ToontaDAO.SurveysListNetworkCallInterface() {
            @Override
            public void onSuccess(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveysListAnswer) {
                newSurveysViewUpdater.onRefreshDone();
                newSurveysViewUpdater.onNewSurveys(surveysListAnswer, true);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                newSurveysViewUpdater.onRefreshDone();
                newSurveysViewUpdater.onFailure(getDescriptionForError(error));
            }
        });
    }

    public void submitLoadPage(int page) {
        if (page > currentPage) {
            newSurveysViewUpdater.onRefreshProgress();
            currentPage++;
            ToontaDAO.getSurveys(currentPage, new ToontaDAO.SurveysListNetworkCallInterface() {
                @Override
                public void onSuccess(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveysListAnswer) {
                    newSurveysViewUpdater.onRefreshDone();
                    newSurveysViewUpdater.onNewSurveys(surveysListAnswer, false);
                }

                @Override
                public void onFailure(ToontaDAO.NetworkAnswer error) {
                    newSurveysViewUpdater.onRefreshDone();
                    newSurveysViewUpdater.onFailure(getDescriptionForError(error));
                }
            });
        }
    }

    /**
     * Fetches all survies
     */
    public void fetchAllSurvies() {
        int page = 1;
        ToontaDAO.getSurveys(page, new ToontaDAO.SurveysListNetworkCallInterface() {
            @Override
            public void onSuccess(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveysListAnswer) {
                newSurveysViewUpdater.onPopulateSurvies(surveysListAnswer);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                newSurveysViewUpdater.onFailure(getDescriptionForError(error));
            }
        });
    }

    /**
     * Fetches one survey
     * @param surveyId
     *          Id of the survey to fetch
     */
    public void fetchSurvey(String surveyId) {
        ToontaDAO.getSurvey(surveyId, new ToontaDAO.SurveyNetworkCallInterface(){
            @Override
            public void onSuccess(ToontaDAO.QuestionsList questionsList) {
                oneSurveyViewUpdator.onGetSurvey(questionsList);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                oneSurveyViewUpdator.onFailure(getDescriptionForError(error));
            }
        });
    }

    public void postSurveyResponse(final SurveyResponse surveyResponse) {
        if (surveyResponse != null) {
            ToontaDAO.postSurveyResponse(surveyResponse, new ToontaDAO.SurveyPostNetworkCallInterface() {
                @Override
                public void onSuccess() {
                    oneSurveyViewUpdator.onPostResponse("200");
                }

                @Override
                public void onFailure(ToontaDAO.NetworkAnswer error) {
                    oneSurveyViewUpdator.onFailure(getDescriptionForError(error));
                }
            });
        }
    }

    //Own methods

    private String getDescriptionForError(ToontaDAO.NetworkAnswer networkAnswer) {
        if (networkAnswer == ToontaDAO.NetworkAnswer.AUTH_FAILURE) {
            return context.getString(R.string.string_error_auth_failure);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.NO_SERVER) {
            return context.getString(R.string.string_error_no_server);
        } else {
            return context.getString(R.string.string_error_no_network);
        }
    }
}
