package com.xvzan.bettermoneytracker.ui.currencies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mCurrency;
import com.xvzan.bettermoneytracker.ui.share.ItemTouchHelperAdapter;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class Adapter_Currency extends RecyclerView.Adapter<Adapter_Currency.Currency_Holder> implements ItemTouchHelperAdapter {

    private Context mContext;
    private Realm realm;
    private OrderedRealmCollection<mCurrency> currencies;

    @NonNull
    @Override
    public Currency_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Currency_Holder(LayoutInflater.from(mContext).inflate(R.layout.currency_in_edit_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Currency_Holder holder, int position) {
        holder.tv_Name.setText(currencies.get(position).getName());
        holder.tv_Pattern.setText(currencies.get(position).getPattern());
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    public Adapter_Currency(Context context, Realm instance) {
        mContext = context;
        realm = instance;
        currencies = realm.where(mCurrency.class).findAll().sort("order", Sort.ASCENDING);
    }

    private Adapter_Currency getAdapter() {
        return this;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mCurrency aa = currencies.get(fromPosition);
        mCurrency ab = currencies.get(toPosition);
        realm.beginTransaction();
        aa.setOrder(toPosition);
        ab.setOrder(fromPosition);
        realm.commitTransaction();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {

    }

    @Override
    public void afterMoved() {
        realm.beginTransaction();
        for (mCurrency currency : currencies) {
            currency.setOrder(currencies.indexOf(currency));
        }
        realm.commitTransaction();
        notifyDataSetChanged();
    }

    class Currency_Holder extends RecyclerView.ViewHolder {
        TextView tv_Pattern;
        TextView tv_Name;
        ImageButton ib_Edit;
        ImageButton ib_Delete;

        Currency_Holder(View itemView) {
            super(itemView);
            tv_Pattern = itemView.findViewById(R.id.tv_Symbol);
            tv_Name = itemView.findViewById(R.id.tv_CurrencyInEditList);
            ib_Edit = itemView.findViewById(R.id.bt_EditCurrency);
            ib_Delete = itemView.findViewById(R.id.bt_DeleteCurrency);
            ib_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddCurrencyDialogFragment edf = new AddCurrencyDialogFragment(getAdapter(), tv_Name.getText().toString());
                    edf.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "edit_currency_dialog");
                }
            });
            ib_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try (Realm realm = Realm.getDefaultInstance()) {
                        if (realm.where(mAccount.class).equalTo("currency.name", tv_Name.getText().toString()).findAll().size() > 0) {
                            Toast.makeText(mContext, "Cannot delete currency which is in use", Toast.LENGTH_SHORT).show();
                        } else {
                            mCurrency ma = realm.where(mCurrency.class).equalTo("name", tv_Name.getText().toString()).findFirst();
                            assert ma != null;
                            realm.beginTransaction();
                            ma.deleteFromRealm();
                            realm.commitTransaction();
                            afterMoved();
                        }
                    }
                }
            });
        }
    }
}
