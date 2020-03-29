package com.xvzan.bettermoneytracker.ui.exportandimport;

import android.annotation.SuppressLint;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class ExportDialogFragment extends DialogFragment {

    private File csvC;
    private File csvA;
    private File csvT;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.export_dialog_gragment, container);
        TextView tvf = view.findViewById(R.id.tv_export_folder);
        csvC = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(null), "currencies");
        csvA = new File(getContext().getExternalFilesDir(null), "accounts");
        csvT = new File(getContext().getExternalFilesDir(null), "transactions");
        tvf.setText("Output Folder: " + Objects.requireNonNull(getContext().getExternalFilesDir(null)).getAbsolutePath());
        Button bte = view.findViewById(R.id.bt_export);
        bte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCSV();
            }
        });
        return view;
    }

    private void exportCSV() {
        if (csvC.exists())
            csvC.delete();
        csvC = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(null), "currencies");
        if (csvA.exists())
            csvA.delete();
        csvA = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(null), "accounts");
        if (csvT.exists())
            csvT.delete();
        csvT = new File(getContext().getExternalFilesDir(null), "transactions");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(csvC, true);
            StringBuilder stringBuilder = new StringBuilder();
            try (Realm realm = Realm.getDefaultInstance()) {
                OrderedRealmCollection<mCurrency> mCList = realm.where(mCurrency.class).findAll().sort("order", Sort.ASCENDING);
                for (mCurrency ma : mCList) {
                    stringBuilder.append(ma.getName()).append("\t").append(ma.getPattern()).append("\t").append(ma.getFractionalDigits());
                    stringBuilder.append("\n");
                }
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(csvA, true);
            StringBuilder stringBuilder = new StringBuilder();
            try (Realm realm = Realm.getDefaultInstance()) {
                OrderedRealmCollection<mAccount> mAList = realm.where(mAccount.class).findAll().sort("order", Sort.ASCENDING);
                for (mAccount ma : mAList) {
                    String currencyName = "#InVaild#";
                    if (ma.getAcct() <= 1)
                        currencyName = ma.getCurrency().getName();
                    stringBuilder.append(ma.getAname()).append("\t").append(ma.getAcct()).append("\t").append(currencyName);
                    stringBuilder.append("\n");
                }
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(csvT, true);
            StringBuilder stringBuilder = new StringBuilder();
            try (Realm realm = Realm.getDefaultInstance()) {
                OrderedRealmCollection<mTra> mTraList = realm.where(mTra.class).findAll().sort("mDate", Sort.ASCENDING);
                for (mTra mt : mTraList) {
                    stringBuilder.append(sdf.format(mt.getmDate())).append("\t").append(mt.getAccB().getAname()).append("\t").append(mt.getAccU().getAname()).append("\t").append(mt.getbAm()).append("\t").append(mt.getuAm()).append("\t").append(mt.getmNote());
                    stringBuilder.append("\n");
                }
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "Exported", Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getDialog()).dismiss();
    }
}
