package com.xvzan.bettermoneytracker.ui.exportandimport;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mCurrency;
import com.xvzan.bettermoneytracker.dbsettings.mTra;
import com.xvzan.bettermoneytracker.ui.addaccount.AddAccountDialogFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;

public class ImportDialogfragment extends DialogFragment {

    private File csvC;
    private File csvA;
    private File csvT;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.import_dialog_fragment, container);
        TextView tvc = view.findViewById(R.id.tv_import_cur);
        TextView tva = view.findViewById(R.id.tv_import_acc);
        TextView tvt = view.findViewById(R.id.tv_import_trans);
        Button bti = view.findViewById(R.id.bt_import);
        csvC = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(null), "import" + File.separator + "currencies");
        csvA = new File(getContext().getExternalFilesDir(null), "import" + File.separator + "accounts");
        csvT = new File(getContext().getExternalFilesDir(null), "import" + File.separator + "transactions");
        if (csvC.exists()) {
            tvc.setText("Import currencies from :" + csvC.getAbsolutePath());
        } else {
            tvc.setText(csvC.getAbsolutePath() + " not found!");
            tvc.setTextColor(Color.RED);
        }
        if (csvA.exists()) {
            tva.setText("Import accounts from :" + csvA.getAbsolutePath());
        } else {
            tva.setText(csvA.getAbsolutePath() + " not found!");
            tva.setTextColor(Color.RED);
        }
        if (csvT.exists()) {
            tvt.setText("Import transactions from : " + csvT.getAbsolutePath());
        } else {
            tvt.setText(csvT.getAbsolutePath() + " not found");
            tvt.setTextColor(Color.RED);
        }
        if (csvA.exists() || csvC.exists())
            bti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    importCSV();
                }
            });
        return view;
    }

    private void importCSV() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try (Realm realm = Realm.getDefaultInstance()) {
            try {
                BufferedReader bufferedReader;
                int s;
                boolean isSimple = false;
                String line;
                if (!csvC.exists() && realm.where(mCurrency.class).findAll().size() == 0) {
                    isSimple = true;
                    mCurrency fc = new mCurrency();
                    Currency currency = Currency.getInstance(Locale.getDefault());
                    fc.setALL(currency.getSymbol(), currency.getCurrencyCode(), ((DecimalFormat) NumberFormat.getCurrencyInstance()).toPattern(), currency.getDefaultFractionDigits());
                    fc.setOrder(1);
                    realm.beginTransaction();
                    realm.copyToRealm(fc);
                    realm.commitTransaction();
                } else {
                    bufferedReader = new BufferedReader(new FileReader(csvC));
                    s = 1;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] item = line.split("\t");
                        mCurrency mc = new mCurrency();
                        mc.setALL("", item[0], item[1], Integer.parseInt(item[2]));
                        mc.setOrder(s);
                        realm.beginTransaction();
                        realm.copyToRealm(mc);
                        realm.commitTransaction();
                        s++;
                    }
                    csvC.delete();
                }
                if (csvA.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(csvA));
                    AddAccountDialogFragment.addAccountListener listener = (AddAccountDialogFragment.addAccountListener) getActivity();
                    s = 1;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] item = line.split("\t");
                        mAccount ma = new mAccount();
                        ma.setAname(item[0]);
                        ma.setAType(Integer.parseInt(item[1]));
                        if (Integer.parseInt(item[1]) <= 1)
                            if (!isSimple)
                                ma.setCurrency(realm.where(mCurrency.class).equalTo("name", item[2]).findFirst());
                            else ma.setCurrency(realm.where(mCurrency.class).findFirst());
                        ma.setOrder(s);
                        realm.beginTransaction();
                        realm.copyToRealm(ma);
                        realm.commitTransaction();
                        s++;
                    }
                    assert listener != null;
                    listener.onAccountsEdited();
                    csvA.delete();
                }
                if (csvT.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(csvT));
                    if (!isSimple) {
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] item = line.split("\t");
                            mTra ts = new mTra();
                            //ts.directSet(realm.where(mAccount.class).equalTo("aname", item[2]).findFirst(), realm.where(mAccount.class).equalTo("aname", item[1]).findFirst(), Long.parseLong(item[4]), Long.parseLong(item[3]), sdf.parse(item[0]), item[5]);
                            ts.directSet(realm.where(mAccount.class).equalTo("aname", item[2]).findFirst(), realm.where(mAccount.class).equalTo("aname", item[1]).findFirst(), Long.parseLong(item[4]), Long.parseLong(item[3]), sdf.parse(item[0]));
                            if (item.length == 6)
                                ts.setmNote(item[5]);
                            else
                                ts.setmNote("");
                            realm.beginTransaction();
                            realm.copyToRealm(ts);
                            realm.commitTransaction();
                        }
                    } else {
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] item = line.split("\t");
                            mTra ts = new mTra();
                            //ts.directSet(realm.where(mAccount.class).equalTo("aname", item[2]).findFirst(), realm.where(mAccount.class).equalTo("aname", item[1]).findFirst(), Long.parseLong(item[4]), Long.parseLong(item[3]), sdf.parse(item[0]), item[5]);
                            ts.directSet(realm.where(mAccount.class).equalTo("aname", item[2]).findFirst(), realm.where(mAccount.class).equalTo("aname", item[1]).findFirst(), Long.parseLong(item[4]), Long.parseLong(item[5]), sdf.parse(item[0]));
                            if (item.length == 6)
                                ts.setmNote(item[5]);
                            else
                                ts.setmNote("");
                            realm.beginTransaction();
                            realm.copyToRealm(ts);
                            realm.commitTransaction();
                        }
                    }
                    csvT.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getContext(), "Imported", Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getDialog()).dismiss();
    }
}
