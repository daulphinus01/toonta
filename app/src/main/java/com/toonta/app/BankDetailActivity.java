package com.toonta.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.toonta.app.model.Bank;
import com.toonta.app.utils.BankDetailAdapter;

import java.util.ArrayList;
import java.util.List;

public class BankDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        // Actionbar
        setupActionBar();

        TextView labelToons = (TextView) findViewById(R.id.bank_total_qty);

        ListView listView = (ListView) findViewById(R.id.bank_items);

        List<Bank> bankList = generateSurveys();
        assert labelToons != null;
        labelToons.setText(computeBanksTotalToons(bankList));
        labelToons.setTransformationMethod(null);

        BankDetailAdapter bankDetailAdapter = new BankDetailAdapter(getBaseContext(), bankList);
        assert listView != null;
        listView.setAdapter(bankDetailAdapter);
    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private List<Bank> generateSurveys() {
        List<Bank> banks = new ArrayList<>();
        banks.add(new Bank("Total Senegal", "", 74));
        banks.add(new Bank("Grands Moulins", "", 412));
        banks.add(new Bank("MTN Corporation", "", 87));
        banks.add(new Bank("BICIS Banque", "", 365));
        banks.add(new Bank("Toto from Sg banque", "", 111));
        banks.add(new Bank("Presidential standard banque", "", 32));
        return banks;
    }

    private String computeBanksTotalToons(List<Bank> bankList) {
        assert bankList != null;
        int total = 0;
        for (Bank bank : bankList) {
            total += bank.getTotalToons();
        }
        return total + " toons";
    }
}
