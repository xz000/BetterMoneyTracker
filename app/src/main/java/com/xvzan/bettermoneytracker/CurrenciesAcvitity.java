package com.xvzan.bettermoneytracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.ui.currencies.Adapter_Currency;
import com.xvzan.bettermoneytracker.ui.currencies.AddCurrencyDialogFragment;
import com.xvzan.bettermoneytracker.ui.share.SimpleItemTouchHelperCallback;

import io.realm.Realm;

public class CurrenciesAcvitity extends AppCompatActivity {

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_currencies);
        Toolbar myChildToolbar =
                findViewById(R.id.toolbar_balances);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        Button bottomButton = findViewById(R.id.buttonAddAccount);
        bottomButton.setText(R.string.add_currency);
        RecyclerView recyclerView = findViewById(R.id.accRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final Adapter_Currency adapter_currency = new Adapter_Currency(this, realm);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter_currency);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter_currency);
        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCurrencyDialogFragment acd = new AddCurrencyDialogFragment(adapter_currency);
                acd.show(getSupportFragmentManager(), "add_currency_dialog");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
