package com.xvzan.bettermoneytracker.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mCurrency;
import com.xvzan.bettermoneytracker.dbsettings.mTra;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class Adapter_Single extends RecyclerView.Adapter<Adapter_Single.SingleTraHolder> implements FastScroller.BubbleTextGetter {

    private Context mContext;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
    //private NumberFormat numberFormat;
    private double d_Double;
    private OrderedRealmCollection<mTra> mTraList;
    Long[] longs;
    private Long[] tempLongs;
    private String accstr;
    private Realm realminstance;

    Adapter_Single(Context context, String str, Realm instance) {
        this.mContext = context;
        realminstance = instance;
        accstr = str;
        //mCurrency currency = realminstance.where(mAccount.class).equalTo("aname", accstr).findFirst().getCurrency();
        mTraList = realminstance.where(mTra.class).equalTo("accU.aname", accstr).or().equalTo("accB.aname", accstr).findAll().sort("mDate", Sort.ASCENDING);
        if (mTraList.size() >= 512) {
            tempLongs = new Long[32];
            tempLongs[0] = realminstance.where(mTra.class).equalTo("accU.aname", accstr).findAllAsync().sum("uAm").longValue() + realminstance.where(mTra.class).equalTo("accB.aname", accstr).findAllAsync().sum("bAm").longValue();
            for (int i = 0; i < 31; i++) {
                tempLongs[i + 1] = tempLongs[i] - getAmount(mTraList.size() - i - 1);
            }
        }
    }

    @Override
    public Date getDateToShowInBubble(final int pos) {
        return mTraList.get(pos).getmDate();
    }

    @NonNull
    @Override
    public Adapter_Single.SingleTraHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingleTraHolder(LayoutInflater.from(mContext).inflate(R.layout.transaction_single, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Single.SingleTraHolder holder, final int position) {
        double d_Double;
        double o_double;
        mCurrency currency;
        if (mTraList.get(position).getAccU().getAname().equals(accstr)) {
            if (mTraList.get(position).getAccU().getCurrency() != null)
                currency = mTraList.get(position).getAccU().getCurrency();
            else
                currency = mTraList.get(position).getAccB().getCurrency();
            o_double = mTraList.get(position).getuAm();
            holder.tsAccount.setText(mTraList.get(position).getAccB().getAname());
        } else {
            if (mTraList.get(position).getAccB().getCurrency() != null)
                currency = mTraList.get(position).getAccB().getCurrency();
            else
                currency = mTraList.get(position).getAccU().getCurrency();
            o_double = mTraList.get(position).getbAm();
            holder.tsAccount.setText(mTraList.get(position).getAccU().getAname());
        }
        if (o_double < 0)
            holder.tsAmount.setTextColor(Color.RED);
        else
            holder.tsAmount.setTextColor(holder.tsDate.getTextColors());
        d_Double = Math.pow(10d, currency.getFractionalDigits());
        holder.format = new DecimalFormat(currency.getPattern());
        holder.tsAmount.setText(holder.format.format(o_double / d_Double));
        if (longs == null) {
            if (tempLongs != null && tempLongs[0] != null) {
                if (mTraList.size() - position < 32) {
                    if (tempLongs[mTraList.size() - position - 1] < 0)
                        holder.tsTotal.setTextColor(Color.RED);
                    else
                        holder.tsTotal.setTextColor(holder.tsDate.getTextColors());
                    holder.tsTotal.setText(holder.format.format(tempLongs[mTraList.size() - position - 1] / d_Double));
                } else {
                    holder.tsTotal.setText(R.string.calculating);
                }
            }
        } else if (longs[position] != null) {
            if (longs[position] < 0)
                holder.tsTotal.setTextColor(Color.RED);
            else
                holder.tsTotal.setTextColor(holder.tsDate.getTextColors());
            holder.tsTotal.setText(holder.format.format(longs[position] / d_Double));
        }
        holder.tsDate.setText(sdf.format(mTraList.get(position).getmDate()));
        holder.tsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (Realm realm = Realm.getDefaultInstance()) {
                    realm.beginTransaction();
                    mTraList.get(position).setEditme();
                    realm.commitTransaction();
                }
                Navigation.findNavController(v).navigate(R.id.nav_edit_tran);
            }
        });
    }

    private Long getAmount(int pos) {
        if (mTraList.get(pos).getAccU().getAname().equals(accstr)) {
            return mTraList.get(pos).getuAm();
        } else {
            return mTraList.get(pos).getbAm();
        }
    }

    @Override
    public int getItemCount() {
        return mTraList.size();
    }

    static class SingleTraHolder extends RecyclerView.ViewHolder {
        TextView tsDate;
        TextView tsAccount;
        TextView tsAmount;
        TextView tsTotal;
        ImageButton tsEdit;
        NumberFormat format;

        SingleTraHolder(View itemView) {
            super(itemView);
            tsDate = itemView.findViewById(R.id.tsDate);
            tsAccount = itemView.findViewById(R.id.tsAccount);
            tsAmount = itemView.findViewById(R.id.tsAmount);
            tsTotal = itemView.findViewById(R.id.tsTotal);
            tsEdit = itemView.findViewById(R.id.bt_ts_edit);
        }
    }
}
