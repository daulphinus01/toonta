package com.toonta.app.activities.new_surveys;

import android.content.Context;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;

import java.util.List;

public class QuestionReportInteractor {

    private final static String TAG = "QuestionReportInteractor";

    public interface QuestionReportGetter {
        void onQuestionReportSuccess(List<String> reponseDUneQuestion);
        void onFailure(String error);
    }

    QuestionReportGetter questionReportGetter;

    Context context;

    public QuestionReportInteractor(Context context, QuestionReportGetter questionReportGetter) {
        this.questionReportGetter = questionReportGetter;
        this.context = context;;
    }

    public void getQuestionReportByQuestionId(String questionId) {
        ToontaDAO.getQuestionReportByQuestionId(questionId, new ToontaDAO.ReportSimpleNetworkCallInterface() {
            @Override
            public void onSuccess(List<String> reponsesToQuestion) {
                questionReportGetter.onQuestionReportSuccess(reponsesToQuestion);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                questionReportGetter.onFailure(getDescriptionForError(error));
            }
        });
    }

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
