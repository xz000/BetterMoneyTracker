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
import com.xvzan.bettermoneytracker.dbsettings.mTra;
import com.xvzan.bettermoneytracker.ui.share.LinearAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.Sort;

public class EditAccountDialogFragment extends DialogFragment {

    private LinearAdapter linearAdapter;
    private String nameBefore;
    private int typeBefore;
    private EditText nan;
    private Spinner saa;
    private Spinner scu;
    private List<String> nameList;

    public EditAccountDialogFragment(String nameb, int typeb, LinearAdapter adapter) {
        nameBefore = nameb;
        typeBefore = typeb;
        linearAdapter = adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_account_dialog, container);
        nan = view.findViewById(R.id.editNewAccountName);
        nan.setText(nameBefore);
        saa = view.findViewById(R.id.spn_AA);
        saa.setSelection(typeBefore);
        scu = view.findViewById(R.id.spn_CU);
        try (Realm realm = Realm.getDefaultInstance()) {
            List<mCurrency> cuList = realm.where(mCurrency.class).findAll().sort("order", Sort.ASCENDING);
            nameList = new ArrayList<>();
            for (mCurrency ma : cuList) {
                nameList.add(ma.getName());
            }
            ArrayAdapter<String> maaa = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.support_simple_spinner_dropdown_item, nameList);
            scu.setAdapter(maaa);
            if (typeBefore < 2) {
                String cName = Objects.requireNonNull(realm.where(mAccount.class).equalTo("aname", nameBefore).findFirst()).getCurrency().getName();
                scu.setSelection(maaa.getPosition(cName));
            }
        }
        view.findViewById(R.id.buttonAddA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAccountDialogFragment.addAccountListener listener = (AddAccountDialogFragment.addAccountListener) getActivity();
                String aName = nan.getText().toString();
                try (Realm realm = Realm.getDefaultInstance()) {
                    mAccount ma = realm.where(mAccount.class).equalTo("aname", nameBefore).findFirst();
                    realm.beginTransaction();
                    if (!aName.equals(nameBefore)) {
                        if (realm.where(mAccount.class).equalTo("aname", aName).findAll().size() != 0) {
                            Toast.makeText(getContext(), "Duplicate name not allowed", Toast.LENGTH_SHORT).show();
                            realm.commitTransaction();
                            return;
                        } else {
                            assert ma != null;
                            ma.setAname(aName);
                        }
                    }
                    assert ma != null;
                    if (saa.getSelectedItemPosition() != ma.getAcct()) {
                        ma.setAType(saa.getSelectedItemPosition());
                        for (mTra m : realm.where(mTra.class).equalTo("accU.aname", ma.getAname()).or().equalTo("accB.aname", ma.getAname()).findAll()) {
                            m.setAllAmount();
                        }
                    }
                    if (saa.getSelectedItemPosition() < 2) {
                        ma.setCurrency(realm.where(mCurrency.class).equalTo("name", nameList.get(scu.getSelectedItemPosition())).findFirst());
                    } else ma.setCurrency(null);
                    realm.commitTransaction();
                    Toast.makeText(getContext(), "Edited " + aName + " as " + getResources().getStringArray(R.array.account_types)[saa.getSelectedItemPosition()], Toast.LENGTH_SHORT).show();
                }
                assert listener != null;
                listener.onAccountsEdited();
                linearAdapter.notifyDataSetChanged();
                Objects.requireNonNull(getDialog()).dismiss();
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
