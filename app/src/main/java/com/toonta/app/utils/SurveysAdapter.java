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
 * @since 1.0.0 [06/06/2016]
 */
public class SurveysAdapter extends ArrayAdapter<ToontaDAO.SurveysListAnswer.SurveyElement> {

    private List<ToontaDAO.SurveysListAnswer.SurveyElement> surveyList;

    public SurveysAdapter(Context context) {
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
        ToontaDAO.SurveysListAnswer.SurveyElement survey = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.textView.setText(survey.name);
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
