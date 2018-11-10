package br.com.elo.lio.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.elo.lio.R;
import br.com.elo.lio.model.User;
import br.com.elo.lio.persistence.UserPersistence;

public class DashboardActivity extends AppCompatActivity {

    private String TAG = "elo.Dashboard";

    private Fragment currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    CurrentFragment current = new CurrentFragment();
                    openFragment(current);
                    return true;
                case R.id.navigation_dashboard:
                    HistoryFragment history = new HistoryFragment();
                    openFragment(history);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        openFragment(new CurrentFragment());
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        currentFragment = fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() != null) {
                Log.d(TAG, "QR code: " + result.getContents());
                try {
                    JSONObject cliente = new JSONObject(result.getContents());
                    User user = new User();
                    user.encode(cliente.toString());
                    UserPersistence.addUser(user);

                    Toast.makeText(this, user.getNome() + " adicionado à lista de compras em " +
                                    "andamento.", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Não foi possível identificar cliente.", Toast
                            .LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Não foi possível identificar cliente.", Toast
                        .LENGTH_LONG).show();
            }
        }
    }
}
