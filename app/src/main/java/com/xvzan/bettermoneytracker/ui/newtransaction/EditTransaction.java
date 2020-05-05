package com.xvzan.bettermoneytracker.ui.newtransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mTra;
import com.xvzan.bettermoneytracker.ui.plantask.PlanTaskDialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.Sort;

public class EditTransaction extends Fragment {

    private List<String> nameList;
    private Spinner aU;
    private Spinner aB;
    private TextView tU;
    private TextView tB;
    private EditText bam;
    private EditText ratio;
    private EditText uam;
    private EditText note;
    private Button dt;
    private Button tm;
    private ImageButton repeatButton;
    private Calendar cld;
    private List<Integer> typeList;
    private mTra myTran;
    private Realm realm;
    private List<mAccount> accList;
    private long aLong;
    private boolean isMultiCurrency;
    private boolean isSwitching = false;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.new_transaction_dialog, container, false);
        boolean isEdit = false;
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        myTran = realm.where(mTra.class).equalTo("editMe", true).findFirst();
        if (myTran != null) {
            myTran.meEdited();
            isEdit = true;
        } else
            root.findViewById(R.id.ib_nt_delete).setVisibility(View.INVISIBLE);
        realm.commitTransaction();
        cld = Calendar.getInstance();
        aU = root.findViewById(R.id.spn_nt_aU);
        aB = root.findViewById(R.id.spn_nt_aB);
        tU = root.findViewById(R.id.tv_nt_aU);
        tB = root.findViewById(R.id.tv_nt_aB);
        repeatButton = root.findViewById(R.id.ib_nt_repeat);
        nameList = new ArrayList<>();
        nameList.add("");
        typeList = new ArrayList<>();
        typeList.add(5);
        accList = realm.where(mAccount.class).findAll().sort("order", Sort.ASCENDING);
        for (mAccount ma : accList) {
            nameList.add(ma.getAname());
            typeList.add(ma.getAcct());
        }
        ArrayAdapter<String> maaa = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.support_simple_spinner_dropdown_item, nameList);
        aU.setAdapter(maaa);
        aB.setAdapter(maaa);
        AdapterView.OnItemSelectedListener spln = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setInputFields();
                setHintTextViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        aU.setOnItemSelectedListener(spln);
        aB.setOnItemSelectedListener(spln);
        bam = root.findViewById(R.id.et_nt_bam);
        uam = root.findViewById(R.id.et_nt_uam);
        ratio = root.findViewById(R.id.et_nt_ratio);
        bam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!bam.hasFocus() || isSwitching)
                    return;
                if (ratio.getText().toString().equals("")) {
                    uam.setText(s.toString());
                } else if (s.toString().equals("")) {
                    uam.setText("");
                } else if (s.toString().contains(".")) {
                    uam.setText(Double.toString(Double.parseDouble(s.toString()) * Double.parseDouble(ratio.getText().toString())));
                } else {
                    uam.setText(Long.toString((long) (Math.pow(10d, aLong) * Double.parseDouble(s.toString()) * Double.parseDouble(ratio.getText().toString()))));
                }
            }
        });
        uam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!uam.hasFocus() || isSwitching)
                    return;
                calculateRatio(s);
            }
        });
        ratio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!ratio.hasFocus() || isSwitching)
                    return;
                if (!bam.getText().toString().equals("")) {
                    if (s.toString().equals(""))
                        uam.setText("");
                    else if (bam.getText().toString().contains(".")) {
                        uam.setText(Double.toString(Double.parseDouble(s.toString()) * Double.parseDouble(bam.getText().toString())));
                    } else
                        uam.setText(Long.toString((long) (Math.pow(10d, aLong) * Double.parseDouble(s.toString()) * Double.parseDouble(bam.getText().toString()))));
                }
            }
        });
        dt = root.findViewById(R.id.bt_nt_Date);
        tm = root.findViewById(R.id.bt_nt_Time);
        note = root.findViewById(R.id.et_nt_note);
        if (isEdit) {
            cld.setTime(myTran.getmDate());
            aU.setSelection(nameList.indexOf(myTran.getAccU().getAname()));
            aB.setSelection(nameList.indexOf(myTran.getAccB().getAname()));
            bam.setText(Long.toString(myTran.getDeltaB()));
            uam.setText(Long.toString(myTran.getDeltaU()));
            note.setText(myTran.getmNote());
            //setInputFields();
        } else {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(root, InputMethodManager.SHOW_IMPLICIT);
            bam.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    InputMethodManager im = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (hasFocus) {
                        assert im != null;
                        im.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        assert im != null;
                        im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }
        if (isMultiCurrency) calculateRatio(uam.getText());
        dt.setText(DateFormat.getDateInstance().format(cld.getTime()));
        tm.setText(DateFormat.getTimeInstance().format(cld.getTime()));
        dt.setOnClickListener(new View.OnClickListener() {
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
                        dt.setText(DateFormat.getDateInstance().format(cld.getTime()));
                    }
                });
                dpd.show();
            }
        });
        tm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        cld.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cld.set(Calendar.MINUTE, minute);
                        tm.setText(DateFormat.getTimeInstance().format(cld.getTime()));
                    }
                }, cld.get(Calendar.HOUR_OF_DAY), cld.get(Calendar.MINUTE), true).show();
            }
        });
        Button ntbt = root.findViewById(R.id.bt_nt);
        final boolean finalIsEdit1 = isEdit;
        ntbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aU.getSelectedItemPosition() == 0 || aB.getSelectedItemPosition() == 0 || aU.getSelectedItemPosition() == aB.getSelectedItemPosition())
                    return;
                String bamstr = bam.getText().toString();
                long bamint;
                long uamint;
                if (bamstr.equals("")) return;
                if (bamstr.contains("."))
                    bamint = (long) (Double.parseDouble(bamstr) * Math.pow(10d, accList.get(aB.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits()));
                else
                    bamint = Long.parseLong(bamstr);
                if (isMultiCurrency) {
                    String uamstr = uam.getText().toString();
                    if (uamstr.equals(""))
                        return;
                    if (uamstr.contains("."))
                        uamint = (long) (Double.parseDouble(uamstr) * Math.pow(10d, accList.get(aU.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits()));
                    else
                        uamint = Long.parseLong(uamstr);
                } else uamint = bamint;
                String tNote = note.getText().toString();
                mAccount uu = accList.get(aU.getSelectedItemPosition() - 1);
                mAccount bb = accList.get(aB.getSelectedItemPosition() - 1);
                if (finalIsEdit1) {
                    realm.beginTransaction();
                    myTran.ubSet(uu, bb, uamint, bamint, cld.getTime());
                    myTran.setmNote(tNote);
                } else {
                    mTra ts = new mTra();
                    ts.ubSet(uu, bb, uamint, bamint, cld.getTime());
                    ts.setmNote(tNote);
                    realm.beginTransaction();
                    realm.copyToRealm(ts);
                }
                realm.commitTransaction();
                Navigation.findNavController(root).navigateUp();
            }
        });
        ImageButton bt_Delete = root.findViewById(R.id.ib_nt_delete);
        final boolean finalIsEdit = isEdit;
        bt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalIsEdit) {
                    realm.beginTransaction();
                    myTran.deleteFromRealm();
                    realm.commitTransaction();
                }
                Navigation.findNavController(root).navigate(R.id.action_nav_edit_tran_to_nav_home);
            }
        });
        ImageButton ib_sw = root.findViewById(R.id.ib_switch);
        ib_sw.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isSwitching = true;
                int temp = aU.getSelectedItemPosition();
                aU.setSelection(aB.getSelectedItemPosition());
                aB.setSelection(temp);
                if (isMultiCurrency) {
                    String tempStr;
                    if (uam.getText().toString().equals(""))
                        tempStr = "";
                    else if (uam.getText().toString().contains("."))
                        tempStr = uam.getText().toString();
                    else
                        tempStr = Double.toString(Double.parseDouble(uam.getText().toString()) /
                                Math.pow(10d, accList.get(aB.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits()));
                    if (bam.getText().toString().equals(""))
                        tempStr = "";
                    else if (bam.getText().toString().contains("."))
                        uam.setText(bam.getText().toString());
                    else
                        uam.setText(Double.toString(Double.parseDouble(bam.getText().toString()) /
                                Math.pow(10d, accList.get(aU.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits())));
                    bam.setText(tempStr);
                    calculateRatio(uam.getText());
                }
                isSwitching = false;
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlanTaskDialogFragment planTaskDialogFragment = new PlanTaskDialogFragment();
                planTaskDialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "edit_repeat_dialog");
            }
        });
        if (isEdit && myTran.hasTask())
            repeatButton.setImageTintList(getContext().getResources().getColorStateList(R.color.repeating, getContext().getTheme()));
        return root;
    }

    @Override
    public void onDestroyView() {
        realm.close();
        super.onDestroyView();
    }

    private void setHintTextViews() {
        if (typeList.get(aU.getSelectedItemPosition()) == -1 || typeList.get(aB.getSelectedItemPosition()) == -1) {
            tU.setText(R.string.category);
            tB.setText(R.string.account);
            return;
        }
        switch (typeList.get(aB.getSelectedItemPosition())) {
            case 2:
            case 3:
                tU.setText(R.string.transfer_to);
                tB.setText(R.string.from_account);
                break;
            default:
                switch (typeList.get(aU.getSelectedItemPosition())) {
                    case 2:
                        tU.setText(R.string.credit_income);
                        tB.setText(R.string.to_account);
                        break;
                    case 3:
                        tU.setText(R.string.pay_expense);
                        tB.setText(R.string.from_account);
                        break;
                    default:
                        tU.setText(R.string.transfer_to);
                        tB.setText(R.string.from_account);
                }
        }
    }

    private void setInputFields() {
        if (typeList.get(aU.getSelectedItemPosition()) >= 2 || typeList.get(aB.getSelectedItemPosition()) >= 2) {
            aLong = 0L;
            showMultiEdit(false);
            return;
        }
        if (!accList.get(aU.getSelectedItemPosition() - 1).getCurrency().equals(accList.get(aB.getSelectedItemPosition() - 1).getCurrency())) {
            aLong = accList.get(aU.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits()
                    - accList.get(aB.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits();
            showMultiEdit(true);
        } else {
            aLong = 0L;
            showMultiEdit(false);
        }
    }

    private void showMultiEdit(boolean show) {
        isMultiCurrency = show;
        if (isMultiCurrency) {
            uam.setVisibility(View.VISIBLE);
            ratio.setVisibility(View.VISIBLE);
        } else {
            uam.setVisibility(View.GONE);
            ratio.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateRatio(Editable s) {
        if (!bam.getText().toString().equals("")) {
            if (s.toString().equals("")) {
                ratio.setText("");
            } else {
                double udouble = (s.toString().contains(".")) ?
                        Double.parseDouble(s.toString()) : Double.parseDouble(s.toString()) /
                        Math.pow(10d, accList.get(aU.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits());
                double bdouble = (bam.getText().toString().contains(".")) ?
                        Double.parseDouble(bam.getText().toString()) : Double.parseDouble(bam.getText().toString()) /
                        Math.pow(10d, accList.get(aB.getSelectedItemPosition() - 1).getCurrency().getFractionalDigits());
                ratio.setText(Double.toString(udouble / bdouble));
            }
        } else
            ratio.setText("");
    }
}
