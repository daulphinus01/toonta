package com.toonta.app.activities.new_surveys;

import android.content.Context;
import android.util.Log;


import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.model.SurveyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 22/05/2016.
 */
public class NewSurveysInteractor {

    private final static String TAG = "NewSurveysInteractor";

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

    public interface SurviesIDsUpdater {
        void onSuccess(boolean existAnsweredId);
        void onFailure(String error);
    }

    public interface CompaniesUpdater {
        void onSuccess(ToontaDAO.SurveysListAnswer existAnsweredId);
        void onFailure(String error);
    }

    NewSurveysViewUpdater newSurveysViewUpdater;
    OneSurveyViewUpdator oneSurveyViewUpdator;
    SurviesIDsUpdater surviesIDsUpdater;
    CompaniesUpdater companiesUpdater;

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

    public NewSurveysInteractor(Context context, SurviesIDsUpdater surviesIDsUpdater) {
        this.context = context;
        this.surviesIDsUpdater = surviesIDsUpdater;
    }

    public NewSurveysInteractor(Context context, CompaniesUpdater companiesUpdater) {
        this.context = context;
        this.companiesUpdater = companiesUpdater;
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
                newSurveysViewUpdater.onPopulateSurvies(getActiveSurvies(surveysListAnswer));
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
                Log.v(TAG, questionsList.toString());
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

    public void existAnsweredId(final String authorId, final String surveyId) {
        if (authorId != null) {
            ToontaDAO.getAnsweredSurveysIds(authorId, new ToontaDAO.SurveysListIDsNetworkCallInterface() {
                @Override
                public void onSuccess(List<String> surveysListAnswer) {
                    Log.v("NewSurveysInteractor", surveysListAnswer.toString());
                    surviesIDsUpdater.onSuccess(surveysListAnswer.contains(surveyId));
                }

                @Override
                public void onFailure(ToontaDAO.NetworkAnswer error) {
                    surviesIDsUpdater.onFailure(getDescriptionForError(error));
                }
            });
        }
    }

    public void getCompanies() {
        ToontaDAO.getCompanies(new ToontaDAO.CompaniesNetworkCallInterface() {
            @Override
            public void onSuccess(ToontaDAO.SurveysListAnswer surveysListAnswer) {
                Log.v("NewSurveysInteractor", surveysListAnswer.toString());
                companiesUpdater.onSuccess(surveysListAnswer);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                companiesUpdater.onFailure(getDescriptionForError(error));
            }
        });
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

    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> getActiveSurvies(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveysListAnswer) {
        ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> tmp = new ArrayList<>();
        for (ToontaDAO.SurveysListAnswer.SurveyElement se : surveysListAnswer) {
            if (se.active) {
                tmp.add(se);
            }
        }
        return tmp;
    }
}
