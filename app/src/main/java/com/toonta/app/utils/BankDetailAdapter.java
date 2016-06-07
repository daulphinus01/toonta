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
import com.toonta.app.model.Bank;

import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [07/06/2016]
 */
public class BankDetailAdapter extends ArrayAdapter<Bank> {

    private int totalBankRow;

    public BankDetailAdapter(Context context, List<Bank> bankList) {
        super(context, 0, bankList);
        this.totalBankRow = bankList.size();
    }

    @Override
    public int getCount() {
        return totalBankRow;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_row, parent, false);
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
        Bank bank = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        int textColor = getContext().getResources().getColor(R.color.screen_1_text);

        viewHolder.bankTitleTextView.setText(bank.getTitle());
        viewHolder.bankTitleTextView.setTransformationMethod(null);
        viewHolder.bankTitleTextView.setTextColor(textColor);

        viewHolder.bankToonsTextView.setText(bank.print());
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
