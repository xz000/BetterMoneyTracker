package com.xvzan.bettermoneytracker.ui.plantask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mPlanTask;

import io.realm.Realm;

public class PlanTaskDialogFragment extends DialogFragment {

    private int orderBefore;
    private boolean isEdit;
    private int interval;
    private int type;
    private boolean hasEndDate;
    private EditText et_interval;
    private Spinner spn_type;
    private CheckBox cb_reverse;
    private CheckBox cb_end_on;
    private Button bt_date_picker;

    public PlanTaskDialogFragment() {

    }

    public PlanTaskDialogFragment(int order) {
        orderBefore = order;
        isEdit = true;
        try (Realm realm = Realm.getDefaultInstance()) {
            mPlanTask planTask = realm.where(mPlanTask.class).equalTo("order", order).findFirst();
            assert planTask != null;
            hasEndDate = planTask.hasEndTime();
            interval = planTask.getLoopInterval();
            type = planTask.getLoopType();
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repeat_detail_dialog_fragment, container);
        et_interval = view.findViewById(R.id.et_rp_interval);
        spn_type = view.findViewById(R.id.spn_rp_type);
        cb_reverse = view.findViewById(R.id.cb_rp_reverse);
        cb_end_on = view.findViewById(R.id.cb_rp_enddate);
        bt_date_picker = view.findViewById(R.id.bt_rp_end_date);
        spn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cb_reverse.setVisibility(View.INVISIBLE);
                switch (position) {
                    case 0:
                        et_interval.setVisibility(View.INVISIBLE);
                        cb_end_on.setVisibility(View.INVISIBLE);
                        bt_date_picker.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        cb_reverse.setVisibility(View.VISIBLE);
                    default:
                        et_interval.setVisibility(View.VISIBLE);
                        cb_end_on.setVisibility(View.VISIBLE);
                        bt_date_picker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cb_reverse.setVisibility(View.INVISIBLE);
            }
        });
        cb_end_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hasEndDate = isChecked;
                if (isChecked)
                    bt_date_picker.setVisibility(View.VISIBLE);
                else
                    bt_date_picker.setVisibility(View.INVISIBLE);
            }
        });
        if (isEdit) {
            spn_type.setSelection(0);
            et_interval.setText(Integer.toString(interval));
            if (hasEndDate)
                cb_end_on.setChecked(true);
            else
                cb_end_on.setChecked(false);
        } else {
            spn_type.setSelection(type - 1);
            et_interval.setText("1");
            cb_end_on.setChecked(false);
        }

        return view;
    }
}
