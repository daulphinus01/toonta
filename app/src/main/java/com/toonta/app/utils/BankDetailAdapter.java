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
public class BankDetailAdapter extends ArrayAdapter<ToontaDAO.SurveysListAnswer.SurveyElement> {

    private List<ToontaDAO.SurveysListAnswer.SurveyElement> surveyList;

    public BankDetailAdapter(Context context) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_row, parent, false);
            convertView.setBackground(getContext().getResources().getDrawable(R.drawable.survey_row_zone_border_bg_bleu_clair));
        }

        BankRowViewHolder viewHolder = (BankRowViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BankRowViewHolder();
            viewHolder.bankTitleTextView = (TextView) convertView.findViewById(R.id.bank_row_title);
            viewHolder.bankPlusSignImageView = (ImageView) convertView.findViewById(R.id.bank_row_icon);
            viewHolder.bankToonsTextView = (TextView) convertView.findViewById(R.id.bank_row_qty);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        ToontaDAO.SurveysListAnswer.SurveyElement survey = getItem(position);

        // getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        //Bank bank = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        int textColor = getContext().getResources().getColor(R.color.screen_1_1_bg);

        String surveyName = (survey.name == null || survey.name.equals("null")) ? "Unknown company" : survey.name;
        viewHolder.bankTitleTextView.setText(surveyName);
        viewHolder.bankTitleTextView.setTransformationMethod(null);
        viewHolder.bankTitleTextView.setTextColor(textColor);

        viewHolder.bankToonsTextView.setText(survey.print());
        viewHolder.bankToonsTextView.setTransformationMethod(null);
        viewHolder.bankToonsTextView.setTextColor(textColor);


        viewHolder.bankPlusSignImageView.setImageResource(R.mipmap.ic_plus_sign);

        return convertView;
    }

    private class BankRowViewHolder {
        public TextView bankTitleTextView;
        public TextView bankToonsTextView;
        public ImageView bankPlusSignImageView;
    }
}
