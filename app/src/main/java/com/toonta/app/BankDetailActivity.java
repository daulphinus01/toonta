package com.toonta.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.toonta.app.model.Bank;
import com.toonta.app.utils.BankDetailAdapter;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BankDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        // Actionbar
        setupActionBar();

        TextView rightLabel = (TextView) findViewById(R.id.right_label);
        TextView leftLabel = (TextView) findViewById(R.id.left_label);

        ListView listView = (ListView) findViewById(R.id.bank_items);

        assert leftLabel != null;
        leftLabel.setText(R.string.bank_solde);
        leftLabel.setTransformationMethod(null);

        List<Bank> bankList = generateSurveys();
        assert rightLabel != null;
        rightLabel.setText(Utils.computeBanksTotalToons(bankList));
        rightLabel.setTransformationMethod(null);



        BankDetailAdapter bankDetailAdapter = new BankDetailAdapter(getBaseContext(), bankList);
        assert listView != null;
        listView.setAdapter(bankDetailAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), BankDetailQstActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
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
        banks.add(new Bank("Presidential standard banque", "", 32));
        return banks;
    }
}
