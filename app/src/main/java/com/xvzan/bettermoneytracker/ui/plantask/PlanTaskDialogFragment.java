package com.xvzan.bettermoneytracker.ui.plantask;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mPlanTask;
import com.xvzan.bettermoneytracker.ui.newtransaction.EditTransaction;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;

public class PlanTaskDialogFragment extends DialogFragment {

    private int orderBefore;
    private boolean isEdit;
    private int interval;
    private Date dateEnd;
    private int type;
    private boolean isReverse;
    private boolean hasEndDate;
    private EditText et_interval;
    private Spinner spn_type;
    private CheckBox cb_reverse;
    private CheckBox cb_end_on;
    private Button bt_date_picker;
    private Button bt_done;
    private EditTransaction editTransaction;
    private DatePickerDialog datePickerDialog;
    private Calendar cld;

    public PlanTaskDialogFragment(EditTransaction et) {
        editTransaction = et;
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

    public void setOldTypeAndInterval(int type_i, int interval_i) {
        type = type_i;
        interval = interval_i;
    }

    public void setCb_reverse() {
        isReverse = true;
    }

    public void setOldEndDate(Date date_d) {
        dateEnd = date_d;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repeat_detail_dialog_fragment, container);
        cld = Calendar.getInstance();
        cld.set(Calendar.HOUR_OF_DAY, 23);
        cld.set(Calendar.MINUTE, 59);
        cld.set(Calendar.SECOND, 59);
        cld.set(Calendar.MILLISECOND, 999);
        et_interval = view.findViewById(R.id.et_rp_interval);
        spn_type = view.findViewById(R.id.spn_rp_type);
        cb_reverse = view.findViewById(R.id.cb_rp_reverse);
        cb_end_on = view.findViewById(R.id.cb_rp_enddate);
        bt_date_picker = view.findViewById(R.id.bt_rp_end_date);
        bt_done = view.findViewById(R.id.bt_rp_done);
        if (type != 0)
            isEdit = true;
        spn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cb_reverse.setVisibility(View.INVISIBLE);
                switch (position) {
                    case 0:
                        et_interval.setVisibility(View.INVISIBLE);
                        cb_end_on.setVisibility(View.INVISIBLE);
                        cb_end_on.setChecked(false);
                        bt_date_picker.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        cb_reverse.setVisibility(View.VISIBLE);
                    default:
                        et_interval.setVisibility(View.VISIBLE);
                        cb_end_on.setVisibility(View.VISIBLE);
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
            spn_type.setSelection(type);
            et_interval.setText(Integer.toString(interval));
            if (isReverse)
                cb_reverse.setChecked(true);
            if (dateEnd != null) {
                cld.setTime(dateEnd);
                hasEndDate = true;
                cb_end_on.setChecked(true);
            } else {
                cb_end_on.setChecked(false);
            }
        } else {
            spn_type.setSelection(type - 1);
            et_interval.setText("1");
            cb_end_on.setChecked(false);
        }
        bt_date_picker.setText(DateFormat.getDateInstance().format(cld.getTime()));
        bt_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(Objects.requireNonNull(getContext()));
                dpd.getDatePicker().init(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

                    }
                });
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cld.set(year, month, dayOfMonth);
                        bt_date_picker.setText(DateFormat.getDateInstance().format(cld.getTime()));
                    }
                });
                dpd.show();
            }
        });
        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTransaction.setRepeatMode(spn_type.getSelectedItemPosition());
                switch (spn_type.getSelectedItemPosition()) {
                    case 0:
                        break;
                    case 3:
                        editTransaction.setMonthReverse(cb_reverse.isChecked());
                    default:
                        editTransaction.setRepeatInt(Integer.parseInt(et_interval.getText().toString()));
                        if (hasEndDate)
                            editTransaction.setEndDate(cld.getTime());
                }
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
        return view;
    }
}
