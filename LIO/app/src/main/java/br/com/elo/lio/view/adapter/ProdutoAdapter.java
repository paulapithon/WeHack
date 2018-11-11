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
import br.com.elo.lio.model.Produto;

public class ProdutoAdapter extends ArrayAdapter<Produto> {

    private List<Produto> produtoList;

    public ProdutoAdapter(@NonNull Context context, int resource, @NonNull List<Produto> objects) {
        super(context, resource, objects);

        this.produtoList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_produto, parent, false);
            holder.name = convertView.findViewById(R.id.prod_nome);
            holder.valor = convertView.findViewById(R.id.prod_valor);
            holder.quantidade = convertView.findViewById(R.id.prod_qtd);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Produto produto = produtoList.get(position);
        holder.name.setText(produto.getNome());
        holder.valor.setText(produto.getPrintValor());
        holder.quantidade.setText(Integer.toString(produto.getQuantidade()) + " x");

        return convertView;
    }

    private class ViewHolder {

        private TextView name;
        private TextView valor;
        private TextView quantidade;

    }


}
