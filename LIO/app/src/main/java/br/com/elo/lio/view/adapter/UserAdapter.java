package br.com.elo.lio.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.elo.lio.R;
import br.com.elo.lio.model.User;

/**
 * LG ELECTRONICS INC.
 * LGEBR - SCA R&D, Sao Paulo, SP, Brazil
 * LGEMR - Santa Clara, CA, USA
 * Copyright(c) 2017 by LG Electronics Inc.
 * <p>
 * <p>
 * All rights reserved. No part of this work may be reproduced, stored in a retrieval system, or transmitted by any
 * means without prior written Permission of LG Electronics Inc.
 */
public class UserAdapter extends ArrayAdapter<User> {

    private List<User> userList;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);

        this.userList = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            holder.name = convertView.findViewById(R.id.user_item_name);
            holder.cpf = convertView.findViewById(R.id.user_item_cpf);
            holder.wallet = convertView.findViewById(R.id.user_item_wallet);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);
        holder.name.setText(user.getNome());
        holder.cpf.setText(user.getCPF());
        holder.wallet.setText(Integer.toString(user.getWallet()));

        return convertView;
    }

    private class ViewHolder {

            private TextView name;
            private TextView cpf;
            private TextView wallet;

    }


}
