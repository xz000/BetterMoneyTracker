package com.xvzan.bettermoneytracker.ui.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mTra;
import com.xvzan.bettermoneytracker.ui.addaccount.EditAccountDialogFragment;

import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> implements ItemTouchHelperAdapter {

    private Context mContext;
    //private List<String> strlist=new ArrayList<>();
    private OrderedRealmCollection<mAccount> mAList;
    private Realm realmInstance;
    //private List<mAccount> maList = new ArrayList<>();
    private final StartDragListener mStartDragListener;

    LinearAdapter(Context context, StartDragListener startDragListener, Realm instance) {
        this.mContext = context;
        mStartDragListener = startDragListener;
        realmInstance = instance;
        mAList = realmInstance.where(mAccount.class).findAll().sort("order", Sort.ASCENDING);
    }

    @NonNull
    @Override
    public LinearAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.account_in_edit_list, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final LinearAdapter.LinearViewHolder holder, int position) {
        holder.tv_AccountName.setText(mAList.get(position).getAname());

        holder.iv_Handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    mStartDragListener.requestDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAList.size();
    }

    @Override
    public void afterMoved() {
        realmInstance.beginTransaction();
        for (mAccount account : mAList) {
            account.setOrder(mAList.indexOf(account));
        }
        realmInstance.commitTransaction();
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mAccount aa = mAList.get(fromPosition);
        mAccount ab = mAList.get(toPosition);
        realmInstance.beginTransaction();
        aa.setOrder(toPosition);
        ab.setOrder(fromPosition);
        realmInstance.commitTransaction();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {

    }

    private LinearAdapter getAdapter() {
        return this;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        TextView tv_AccountName;
        ImageView iv_Handle;
        ImageButton bt_EditAccount;
        ImageButton bt_DeleteAccount;

        LinearViewHolder(View itemView) {
            super(itemView);
            iv_Handle = itemView.findViewById(R.id.handle_acc_in_list);
            tv_AccountName = itemView.findViewById(R.id.textAccInEditList);
            bt_EditAccount = itemView.findViewById(R.id.buttonEditAccount);
            bt_DeleteAccount = itemView.findViewById(R.id.buttonDeleteAccount);
            bt_DeleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try (Realm realm = Realm.getDefaultInstance()) {
                        if (realm.where(mTra.class).equalTo("accU.aname", tv_AccountName.getText().toString()).or().equalTo("accB.aname", tv_AccountName.getText().toString()).findAll().size() > 0) {
                            Toast.makeText(mContext, "Cannot delete Account which is in use", Toast.LENGTH_SHORT).show();
                            //Cannot delete Account which is in use
                        } else {
                            mAccount ma = realm.where(mAccount.class).equalTo("aname", tv_AccountName.getText().toString()).findFirst();
                            assert ma != null;
                            if (ma.getAcct() == 4) return;
                            realm.beginTransaction();
                            ma.deleteFromRealm();
                            realm.commitTransaction();
                            mAList = realmInstance.where(mAccount.class).findAll().sort("order", Sort.ASCENDING);
                            afterMoved();
                        }
                    }
                }
            });
            bt_EditAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int t;
                    try (Realm realm = Realm.getDefaultInstance()) {
                        t = Objects.requireNonNull(realm.where(mAccount.class).equalTo("aname", tv_AccountName.getText().toString()).findFirst()).getAcct();
                        if (t == 4) {
                            return;
                        }
                    }
                    EditAccountDialogFragment edf = new EditAccountDialogFragment(tv_AccountName.getText().toString(), t, getAdapter());
                    edf.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "edit_account_dialog");
                }
            });
        }
    }
}
