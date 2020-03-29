package com.xvzan.bettermoneytracker.ui.currencies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mCurrency;

import java.text.DecimalFormat;
import java.util.Objects;

import io.realm.Realm;

public class AddCurrencyDialogFragment extends DialogFragment {
    private EditText et_name;
    private EditText et_symbol;
    private EditText et_pattern;
    private EditText et_digits;
    private String name_before;
    private boolean isEdit;
    private Adapter_Currency adapter;

    public AddCurrencyDialogFragment(Adapter_Currency adapter_currency) {
        this.adapter = adapter_currency;
        isEdit = false;
    }

    AddCurrencyDialogFragment(Adapter_Currency adapter_currency, String name_b) {
        this.adapter = adapter_currency;
        name_before = name_b;
        isEdit = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_currency_dialog, container);
        et_name = view.findViewById(R.id.editCurrencyName);
        et_digits = view.findViewById(R.id.editText_digits);
        et_pattern = view.findViewById(R.id.editText_pattern);
        et_symbol = view.findViewById(R.id.editText_symbol);
        et_symbol.setVisibility(View.GONE);
        if (isEdit) {
            try (Realm realm = Realm.getDefaultInstance()) {
                mCurrency currency = realm.where(mCurrency.class).equalTo("name", name_before).findFirst();
                assert currency != null;
                et_symbol.setText(currency.getSymbol());
                et_pattern.setText(currency.getPattern());
                et_digits.setText(Integer.toString(currency.getFractionalDigits()));
                et_name.setText(currency.getName());
            }
        }
        view.findViewById(R.id.buttonAddC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_digits.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Fractional Digits required", Toast.LENGTH_SHORT).show();
                    return;
                }
                int cDigits = Integer.parseInt(et_digits.getText().toString());
                String cName = et_name.getText().toString();
                if (cName.equals("")) {
                    Toast.makeText(getContext(), "Currency name required", Toast.LENGTH_SHORT).show();
                    return;
                }
                String cSymbol = et_symbol.getText().toString();
                String cPattern = et_pattern.getText().toString();
                try {
                    new DecimalFormat(cPattern);
                } catch (IllegalArgumentException e1) {
                    Toast.makeText(getContext(), "Invalid pattern", Toast.LENGTH_SHORT).show();
                    return;
                }
                try (Realm realm = Realm.getDefaultInstance()) {
                    int s = Math.max(realm.where(mCurrency.class).findAll().size(), 0) + 1;
                    if (!(isEdit && cName.equals(name_before)) && realm.where(mCurrency.class).equalTo("name", cName).findAll().size() != 0) {
                        Toast.makeText(getContext(), "Duplicate name not allowed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mCurrency cur;
                    if (isEdit) {
                        cur = realm.where(mCurrency.class).equalTo("name", name_before).findFirst();
                    } else {
                        cur = new mCurrency();
                    }
                    assert cur != null;
                    if (isEdit) {
                        realm.beginTransaction();
                    }
                    cur.setALL(cSymbol, cName, cPattern, cDigits);
                    if (!isEdit) {
                        cur.setOrder(s);
                        realm.beginTransaction();
                    }
                    realm.copyToRealm(cur);
                    realm.commitTransaction();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), cName + " as " + et_pattern.getText(), Toast.LENGTH_SHORT).show();
                    if (isEdit) {
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }
            }
        });
        return view;
    }
}
