package br.com.elo.lio.view;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.elo.lio.R;
import br.com.elo.lio.api.RetrofitClient;
import br.com.elo.lio.model.Produto;
import br.com.elo.lio.model.User;
import br.com.elo.lio.persistence.HistoryPersistence;
import br.com.elo.lio.persistence.UserPersistence;
import br.com.elo.lio.util.StoreConstants;
import br.com.elo.lio.view.adapter.ProdutoAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserActivity extends AppCompatActivity {

    public String TAG = "elo.User";

    private User user;
    private TextView total;

    private ListView productList;
    private ProdutoAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = (User) getIntent().getSerializableExtra("elo.user");

        TextView name = findViewById(R.id.user_name);
        name.setText(user.getNome());
        TextView cpf = findViewById(R.id.user_cpf);
        cpf.setText(user.getCPF());
        total = findViewById(R.id.user_total);
        total.setText(user.getWallet());

        ImageButton exitBtn = findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExitUser();
            }
        });

        Button payBtn = findViewById(R.id.pay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveUser();
            }
        });

        productList = findViewById(R.id.user_products);
        adapter = new ProdutoAdapter(this, R.layout.item_produto, user.getProdutos());
        productList.setAdapter(adapter);
        productList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {
                List<Produto> produtos = user.getProdutos();
                produtos.remove(position);
                user.setProdutos(produtos);

                updateProduct();
                return true;
            }
        });

        Button addBtn = findViewById(R.id.add_product);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListItems();
            }
        });

    }

    public void openListItems() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        List<String> nomes = new ArrayList<>();
        for (Produto produto : getProdutos()) {
            nomes.add(produto.getNome());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserActivity.this, android.R.layout.select_dialog_item, nomes);

        builder.setTitle("Produtos pré-cadastrados");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Produto> produtos = user.getProdutos();
                produtos.add(getProdutos().get(which));
                user.setProdutos(produtos);
                updateProduct();
            }
        });
        builder.setPositiveButton("Inserir produto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addItem();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    private List<Produto> getProdutos() {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("Água", 2, 1));
        produtos.add(new Produto("RedBull", 8, 1));
        produtos.add(new Produto("Caipirinha", 10, 1));
        produtos.add(new Produto("Porção de fritas", 10, 1));
        produtos.add(new Produto("Iscas de carne", 20, 1));
        return produtos;
    }

    public void addItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_product, null);
        builder.setView(view);
        final EditText nome = view.findViewById(R.id.edit_nome);
        final EditText quantidade = view.findViewById(R.id.edit_qtd);
        final EditText valor = view.findViewById(R.id.edit_valor);

        builder.setTitle("Novo Produto");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Produto produto = new Produto();
                produto.setNome(nome.getText().toString());
                produto.setQuantidade(Integer.parseInt(quantidade.getText().toString()));
                produto.setValor(Integer.parseInt(valor.getText().toString()));

                List<Produto> produtos = user.getProdutos();
                produtos.add(produto);
                user.setProdutos(produtos);
                updateProduct();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void updateProduct() {

        adapter = new ProdutoAdapter(this, R.layout.item_produto, user.getProdutos());
        productList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        total.setText(user.getWallet());
        UserPersistence.addUser(user);

        RetrofitClient.getAPIService().update(
                StoreConstants.AppJson,
                StoreConstants.MerchantId,
                StoreConstants.MerchantKey,
                user.encode()
        ).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {}

            @Override
            public void onFailure(Call<User> call, Throwable t) {}
        });
    }

    public void onSaveUser() {
        RetrofitClient.getAPIService().pay(
                StoreConstants.AppJson,
                StoreConstants.MerchantId,
                StoreConstants.MerchantKey,
                user.encode()
        ).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(UserActivity.this, "Requisição enviada para " + user.getNome() + ".", Toast.LENGTH_LONG).show();
                UserPersistence.removeUser(user.getID());
                user.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
                HistoryPersistence.addUser(user);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Falha ao enviar requisição para " + user.getNome() + ".", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onExitUser() {
        UserPersistence.addUser(user);
        finish();
    }

}
