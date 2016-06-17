package com.toonta.app.activities.new_surveys;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.toonta.app.R;
import com.toonta.app.ToontaDAO;

import java.util.ArrayList;


/**
 * Created by Guillaume on 02/06/2016.
 */
public class SurveysListAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView nameTextView;
        TextView rewardTextView;

        public ViewHolder(View view) {
            this.nameTextView = (TextView) view.findViewById(R.id.activity_surveys_list_element_name);
            this.rewardTextView = (TextView) view.findViewById(R.id.activity_surveys_list_element_reward);
        }
    }

    private Context context;
    private ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList;

    public SurveysListAdapter(Context context) {
        super();
        this.context = context;
        this.surveyElementArrayList = new ArrayList<>();
    }

    public void resetWithList(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
        this.surveyElementArrayList = surveyElementArrayList;
        notifyDataSetChanged();
    }

    public void addElements(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> surveyElementArrayList) {
        this.surveyElementArrayList.addAll(surveyElementArrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return surveyElementArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return surveyElementArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return surveyElementArrayList.get(position).name.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_surveys_list_element, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ToontaDAO.SurveysListAnswer.SurveyElement surveyElement = surveyElementArrayList.get(position);

        viewHolder.nameTextView.setText(surveyElement.name);
        viewHolder.rewardTextView.setText(""+surveyElement.reward);
        return convertView;
    }
}
