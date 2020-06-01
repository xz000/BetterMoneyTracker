package com.xvzan.bettermoneytracker.ui.plantask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.bettermoneytracker.R;
import com.xvzan.bettermoneytracker.dbsettings.mPlanTask;
import com.xvzan.bettermoneytracker.dbsettings.mTra;
import com.xvzan.bettermoneytracker.ui.share.ItemTouchHelperAdapter;

import java.text.DateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmCollection;
import io.realm.Sort;

public class Adapter_Plantasks extends RecyclerView.Adapter<Adapter_Plantasks.Holder> implements ItemTouchHelperAdapter {

    private Context mContext;
    private Realm realm;
    private OrderedRealmCollection<mPlanTask> planTasks;
    private DateFormat dateFormat;

    public Adapter_Plantasks(Context context, Realm instance) {
        dateFormat = DateFormat.getInstance();
        mContext = context;
        realm = instance;
        planTasks = realm.where(mPlanTask.class).findAll().sort("order", Sort.ASCENDING);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.plantask_in_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tv_accB.setText(planTasks.get(position).getAccB().getAname());
        holder.tv_accU.setText(planTasks.get(position).getAccU().getAname());
        if (!planTasks.get(position).getActive())
            holder.tv_NextTime.setText("disabled");
        else
            holder.tv_NextTime.setText(dateFormat.format(planTasks.get(position).getNextTime()));
        holder.tv_bAm.setText(Long.toString(planTasks.get(position).getbAm()));
    }

    @Override
    public int getItemCount() {
        return planTasks.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mPlanTask aa = planTasks.get(fromPosition);
        mPlanTask ab = planTasks.get(toPosition);
        realm.beginTransaction();
        aa.setOrder(toPosition);
        ab.setOrder(fromPosition);
        realm.commitTransaction();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        if (planTasks.get(position).getActive()) {
            Toast.makeText(mContext, "Cannot delete enabled task", Toast.LENGTH_SHORT).show();
        } else {
            mPlanTask ma = planTasks.get(position);
            assert ma != null;
            realm.beginTransaction();
            RealmCollection<mTra> toEdit = realm.where(mTra.class).equalTo("planTask.order", ma.getOrder()).findAll();
            for (mTra tra : toEdit) {
                tra.setPlanTask(null);
            }
            ma.deleteFromRealm();
            for (mPlanTask planTask : planTasks) {
                planTask.setOrder(planTasks.indexOf(planTask));
            }
            realm.commitTransaction();
        }
        notifyDataSetChanged();
    }

    @Override
    public void afterMoved() {
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv_accU;
        TextView tv_accB;
        TextView tv_NextTime;
        TextView tv_bAm;

        Holder(View itemView) {
            super(itemView);
            tv_accU = itemView.findViewById(R.id.tv_accu);
            tv_accB = itemView.findViewById(R.id.tv_accb);
            tv_NextTime = itemView.findViewById(R.id.tv_next_time);
            tv_bAm = itemView.findViewById(R.id.tv_bam);
        }
    }
}
