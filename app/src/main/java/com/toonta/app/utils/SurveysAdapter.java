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
import com.toonta.app.model.Survey;

import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [06/06/2016]
 */
public class SurveysAdapter extends ArrayAdapter<Survey> {

    private int totalSurvey;

    public SurveysAdapter(Context context, List<Survey> surveyList) {
        super(context, 0, surveyList);
        this.totalSurvey = surveyList.size();
    }

    @Override
    public int getCount() {
        return totalSurvey;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.survey_row,parent, false);
        }

        SurveyViewHolder viewHolder = (SurveyViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new SurveyViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.survey_title);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.survey_icon);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        Survey survey = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.textView.setText(survey.getTitle());
        viewHolder.textView.setTransformationMethod(null);
        viewHolder.textView.setTextColor(getContext().getResources().getColor(R.color.screen_1_text));
        viewHolder.imageView.setImageResource(R.mipmap.ic_plus_sign);

        return convertView;
    }

    private class SurveyViewHolder {
        public TextView textView;
        public ImageView imageView;
    }
}
