package com.xvzan.bettermoneytracker;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.ui.plantask.Adapter_Plantasks;
import com.xvzan.bettermoneytracker.ui.share.SimpleItemTouchHelperCallback;

import io.realm.Realm;

public class PlanTasksActivity extends AppCompatActivity {

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_plantasks);
        Toolbar myChildToolbar =
                findViewById(R.id.toolbar_balances);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.plantaskRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final Adapter_Plantasks adapter_plantasks = new Adapter_Plantasks(this, realm);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter_plantasks, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter_plantasks);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
