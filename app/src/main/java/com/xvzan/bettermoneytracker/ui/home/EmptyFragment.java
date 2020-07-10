package com.xvzan.bettermoneytracker.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.xvzan.bettermoneytracker.BetterMoneyTracker;
import com.xvzan.bettermoneytracker.R;

import java.util.Objects;

import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

import static io.realm.mongodb.Credentials.emailPassword;

public class EmptyFragment extends Fragment {
    private Button button_login;
    private EditText et_mail;
    private EditText et_pw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.login);
        final View root = inflater.inflate(R.layout.fragment_empty, container, false);
        button_login = root.findViewById(R.id.bt_login);
        et_mail = root.findViewById(R.id.et_EmailAddress);
        et_pw = root.findViewById(R.id.et_TextPassword);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateCredentials())
                    return;
                button_login.setEnabled(false);
                ((BetterMoneyTracker) requireActivity().getApplication()).CloudSyncApp.loginAsync(//Credentials.anonymous()
                        emailPassword(et_mail.getText().toString(), et_pw.getText().toString())
                        , new App.Callback<User>() {
                            @Override
                            public void onResult(App.Result<User> result) {
                                button_login.setEnabled(true);
                                if (result.isSuccess())
                                    Navigation.findNavController(root).navigateUp();
                                else
                                    Log.e("0a", result.getError().toString());
                            }
                        });
            }
        });
        return root;
    }

    private boolean validateCredentials() {
        return !(et_mail.getText().toString().isEmpty() || et_pw.getText().toString().isEmpty());
    }
}
