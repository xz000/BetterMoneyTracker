package com.xvzan.bettermoneytracker.ui.addaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mCurrency;
import com.xvzan.bettermoneytracker.ui.share.LinearAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.Sort;

public class AddAccountDialogFragment extends DialogFragment {

    private EditText nan;
    private Spinner saa;
    private Spinner scu;
    private LinearAdapter adapter;
    private List<String> nameList;

    public interface addAccountListener {
        void onAccountsEdited();
    }

    public AddAccountDialogFragment(LinearAdapter linearAdapter) {
        this.adapter = linearAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_account_dialog, container);
        nan = view.findViewById(R.id.editNewAccountName);
        saa = view.findViewById(R.id.spn_AA);
        scu = view.findViewById(R.id.spn_CU);
        try (Realm realm = Realm.getDefaultInstance()) {
            if (realm.where(mCurrency.class).findAll().size() == 0) {
                mCurrency fc = new mCurrency();
                Currency currency = Currency.getInstance(Locale.getDefault());
                fc.setALL(currency.getSymbol(), currency.getCurrencyCode(), ((DecimalFormat) NumberFormat.getCurrencyInstance()).toPattern(), currency.getDefaultFractionDigits());
                fc.setOrder(1);
                realm.beginTransaction();
                realm.copyToRealm(fc);
                realm.commitTransaction();
            }
            List<mCurrency> cuList = realm.where(mCurrency.class).findAll().sort("order", Sort.ASCENDING);
            nameList = new ArrayList<>();
            for (mCurrency ma : cuList) {
                nameList.add(ma.getName());
            }
            ArrayAdapter<String> maaa = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, nameList);
            scu.setAdapter(maaa);
        }
        view.findViewById(R.id.buttonAddA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccountListener listener = (addAccountListener) getActivity();
                String aName = nan.getText().toString();
                if (aName.equals("")) {
                    Toast.makeText(getContext(), "Account name required", Toast.LENGTH_SHORT).show();
                    return;
                }
                try (Realm realm = Realm.getDefaultInstance()) {
                    int s = Math.max(realm.where(mAccount.class).findAll().size(), 0) + 1;
                    if (s == 1) {
                        mAccount fma = new mAccount();
                        fma.setAname("Equity");
                        fma.setAType(4);
                        fma.setOrder(s);
                        s++;
                        realm.beginTransaction();
                        realm.copyToRealm(fma);
                        realm.commitTransaction();
                    }
                    if (realm.where(mAccount.class).equalTo("aname", aName).findAll().size() != 0) {
                        Toast.makeText(getContext(), "Duplicate name not allowed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAccount ma = new mAccount();
                    ma.setAname(aName);
                    ma.setAType(saa.getSelectedItemPosition());
                    if (saa.getSelectedItemPosition() < 2) {
                        ma.setCurrency(realm.where(mCurrency.class).equalTo("name", nameList.get(scu.getSelectedItemPosition())).findFirst());
                    }
                    ma.setOrder(s);
                    realm.beginTransaction();
                    realm.copyToRealm(ma);
                    realm.commitTransaction();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Added " + aName + " as " + getResources().getStringArray(R.array.account_types)[saa.getSelectedItemPosition()], Toast.LENGTH_SHORT).show();
                }
                listener.onAccountsEdited();
            }
        });
        saa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    scu.setVisibility(View.GONE);
                } else {
                    scu.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                scu.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
}
