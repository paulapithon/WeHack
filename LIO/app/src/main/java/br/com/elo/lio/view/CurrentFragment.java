package br.com.elo.lio.view;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;

import br.com.elo.lio.R;
import br.com.elo.lio.persistence.UserPersistence;
import br.com.elo.lio.view.adapter.UserAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {


    public CurrentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_current, container, false);

        FloatingActionButton cameraBtn = root.findViewById(R.id.floatingActionButton);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClick();
            }
        });

        ListView list = root.findViewById(R.id.user_list);
        UserAdapter adapter = new UserAdapter(getContext(), R.layout.item_user, UserPersistence.getAllUsers());
        list.setAdapter(adapter);

        return root;
    }

    private void onCameraClick() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

}
