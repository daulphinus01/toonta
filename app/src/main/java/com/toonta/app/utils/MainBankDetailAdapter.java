/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;

import java.util.ArrayList;
import java.util.List;

import static com.toonta.app.utils.ToontaConstants.BOX_TITLE_TEXT_MAX_LENGTH;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [07/06/2016]
 * @since 1.0.9 [08/03/2017]
 */
public class MainBankDetailAdapter extends ArrayAdapter<ToontaDAO.SurveysListAnswer.SurveyElement> {

    private List<ToontaDAO.SurveysListAnswer.SurveyElement> surveyList;
    private Context context;

    public MainBankDetailAdapter(Context context) {
        super(context, 0);
        this.context = context;
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
            viewHolder.bankDetailRowRemainingAnswers = (TextView) convertView.findViewById(R.id.bank_detail_row_remaining_answers);
            viewHolder.bankDetailRowRewards = (TextView) convertView.findViewById(R.id.bank_detail_row_answers_reward);

            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        ToontaDAO.SurveysListAnswer.SurveyElement survey = getItem(position);

        if (survey.answered) {
            convertView.setBackground(context.getResources().getDrawable(R.drawable.survey_row_zone_border_bg_green));
        } else {
            convertView.setBackground(context.getResources().getDrawable(R.drawable.survey_row_zone_border_bg_bleu_clair));
        }

        // getItem(position) va récupérer l'item [position] de la List<Survey> surveyList
        //Bank bank = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        int textColor = getContext().getResources().getColor(R.color.screen_1_1_bg);

        viewHolder.bankTitleTextView.setText(computeString(survey.name));
        viewHolder.bankTitleTextView.setTransformationMethod(null);
        viewHolder.bankTitleTextView.setTextColor(textColor);

        int remainigAnswers = survey.target - survey.receivedAnswer;
        String answersRemaining = "Answers remaining : " + ((remainigAnswers < 0) ? 0 : remainigAnswers);
        viewHolder.bankDetailRowRemainingAnswers.setText(answersRemaining);
        viewHolder.bankDetailRowRemainingAnswers.setTransformationMethod(null);

        String pointToGain = "Points to gain : " + survey.reward;
        viewHolder.bankDetailRowRewards.setText(pointToGain);
        viewHolder.bankDetailRowRewards.setTransformationMethod(null);

        return convertView;
    }

    private class BankRowViewHolder {
        TextView bankTitleTextView;
        TextView bankDetailRowRemainingAnswers;
        TextView bankDetailRowRewards;
    }

    /**
     * Cette méthode prend en entrée une chaîne de caractère, la découpe si la taille de cette chaîne est superieure
     * à BOX_TITLE_TEXT_MAX_LENGTH en une chaîne c. On concatène à c trois points => c...
     * @param bankTitleText la chaîne à traiter
     * @return la chaîne c avec trois points à la fin ex.: c...
     */
    private String computeString(String bankTitleText) {
        // On retourne la chaîne elle même si sa taille <= BOX_TITLE_TEXT_MAX_LENGTH
        if (bankTitleText.length() <= BOX_TITLE_TEXT_MAX_LENGTH) {
            return bankTitleText;
        }
        int cptr = 0;
        StringBuilder tmpStr = new StringBuilder();
        String[] bankTitleTextSplitted = bankTitleText.split(" ");
        int boxTitleTextLengthForDots = BOX_TITLE_TEXT_MAX_LENGTH - 4;
        // Si la taille du premier mot depasse BOX_TITLE_TEXT_MAX_LENGTH on le découpe
        // et on envoi une partie
        if (bankTitleTextSplitted[cptr].length() > BOX_TITLE_TEXT_MAX_LENGTH) {
            tmpStr.append(bankTitleTextSplitted[cptr].substring(0, boxTitleTextLengthForDots)).append("... ");
            return tmpStr.toString();
        }

        while (tmpStr.length() <= (boxTitleTextLengthForDots - bankTitleTextSplitted[cptr].length())) {
            tmpStr.append(bankTitleTextSplitted[cptr]).append(" ");
            cptr++;
        }
        return tmpStr.append("...").toString();
    }
}
