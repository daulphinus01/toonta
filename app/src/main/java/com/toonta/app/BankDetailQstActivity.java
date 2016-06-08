package com.toonta.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.toonta.app.model.Bank;
import com.toonta.app.utils.BankDetailAdapter;
import com.toonta.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BankDetailQstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail_qst);

        // Actionbar
        setupActionBar();

        TextView rightLabel = (TextView) findViewById(R.id.right_label_qst);
        TextView leftLabel = (TextView) findViewById(R.id.left_label_qst);

        ListView listView = (ListView) findViewById(R.id.bank_items_qst);

        List<Bank> bankList = generateDetailedBank();
        assert leftLabel != null;
        leftLabel.setText(Utils.computeBanksTotalToons(bankList));
        leftLabel.setTransformationMethod(null);

        assert rightLabel != null;
        rightLabel.setText(R.string.grands_moulins);
        rightLabel.setTransformationMethod(null);

        BankDetailAdapter bankDetailAdapter = new BankDetailAdapter(getBaseContext(), bankList);
        assert listView != null;
        listView.setAdapter(bankDetailAdapter);
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

    private List<Bank> generateDetailedBank() {
        List<Bank> banks = new ArrayList<>();
        banks.add(new Bank("Daily products & Urban zones", "", 90));
        banks.add(new Bank("Colors & Value in packaging", "", 50));
        banks.add(new Bank("Local rice appeal vs price", "", 70));
        banks.add(new Bank("Acts & Relationships in buying", "", 100));
        banks.add(new Bank("Biscao Biscuits & Kids' taste", "", 60));
        banks.add(new Bank("Loyalty bonus", "", 32));
        return banks;
    }
}
