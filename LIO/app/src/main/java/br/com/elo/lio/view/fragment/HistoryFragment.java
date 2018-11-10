package br.com.elo.lio.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;

import br.com.elo.lio.R;
import br.com.elo.lio.persistence.HistoryPersistence;
import br.com.elo.lio.persistence.UserPersistence;
import br.com.elo.lio.view.UserActivity;
import br.com.elo.lio.view.adapter.UserAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private UserAdapter adapter;
    private ListView list;


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        list = root.findViewById(R.id.history_list);
        adapter = new UserAdapter(getContext(), R.layout.item_user, HistoryPersistence.getAllUsers());
        list.setAdapter(adapter);

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onListClick(position);
//            }
//        });

        return root;
    }

    private void onListClick(int position) {
        Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra("elo.user", UserPersistence.getAllUsers().get(position));
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter = new UserAdapter(getContext(), R.layout.item_user, HistoryPersistence.getAllUsers());
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
