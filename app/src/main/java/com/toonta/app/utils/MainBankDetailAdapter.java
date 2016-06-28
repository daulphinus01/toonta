/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [07/06/2016]
 */
public class MainBankDetailAdapter extends ArrayAdapter<ToontaDAO.SurveysListAnswer.SurveyElement> {

    private List<ToontaDAO.SurveysListAnswer.SurveyElement> surveyList;

    public MainBankDetailAdapter(Context context) {
        super(context, 0);
        this.surveyList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return surveyList.size();
    }

    @Override
    public long getItemId(int position) {
        return surveyList.get(position).name.hashCode();
    }

    @Override
    public ToontaDAO.SurveysListAnswer.SurveyElement getItem(int position) {
        return surveyList.get(position);
    }

    public void addElements(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
        this.surveyList.addAll(surveyElementArrayList);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_detail_row, parent, false);
        }

        BankRowViewHolder viewHolder = (BankRowViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BankRowViewHolder();
            viewHolder.bankTitleTextView = (TextView) convertView.findViewById(R.id.bank_detail_row_title);
            viewHolder.bankPlusSignImageView = (ImageView) convertView.findViewById(R.id.bank_row_detail_icon);
            viewHolder.bankDetailRowRemainingAnswers = (TextView) convertView.findViewById(R.id.bank_detail_row_remaining_answers);
            viewHolder.bankDetailRowRewards = (TextView) convertView.findViewById(R.id.bank_detail_row_answers_reward);

            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        ToontaDAO.SurveysListAnswer.SurveyElement survey = getItem(position);

        // getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        //Bank bank = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        int textColor = getContext().getResources().getColor(R.color.screen_1_text);

        viewHolder.bankTitleTextView.setText(survey.name);
        viewHolder.bankTitleTextView.setTransformationMethod(null);
        viewHolder.bankTitleTextView.setTextColor(textColor);

        viewHolder.bankPlusSignImageView.setImageResource(R.mipmap.ic_plus_sign);

        String answersRemaining = "Answers remaining : " + (survey.target - survey.receivedAnswer);
        viewHolder.bankDetailRowRemainingAnswers.setText(answersRemaining);
        viewHolder.bankDetailRowRemainingAnswers.setTransformationMethod(null);

        String pointToGain = "Points to gain : " + survey.reward;
        viewHolder.bankDetailRowRewards.setText(pointToGain);
        viewHolder.bankDetailRowRewards.setTransformationMethod(null);

        return convertView;
    }

    private class BankRowViewHolder {
        public TextView bankTitleTextView;
        public TextView bankDetailRowRemainingAnswers;
        public TextView bankDetailRowRewards;
        public ImageView bankPlusSignImageView;
    }
}
