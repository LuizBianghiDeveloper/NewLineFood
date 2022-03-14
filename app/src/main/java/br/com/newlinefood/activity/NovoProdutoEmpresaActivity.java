package br.com.newlinefood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.newlinefood.R;
import br.com.newlinefood.helper.UsuarioFirebase;
import br.com.newlinefood.model.Empresa;
import br.com.newlinefood.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText edtNomeProduto, edtDescricao, edtProdutoPreco;
    private FirebaseAuth autenticacao;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
    }

    public void validarDadosProduto (View view){
        String NomeProduto = edtNomeProduto.getText().toString();
        String Descricao = edtDescricao.getText().toString();
        String ProdutoPreco = edtProdutoPreco.getText().toString();

        if (!NomeProduto.isEmpty()){
            if (!Descricao.isEmpty()){
                if (!ProdutoPreco.isEmpty()){
                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(NomeProduto);
                    produto.setPreco(Double.parseDouble(ProdutoPreco));
                    produto.setDescricao(Descricao);
                    produto.salvar() ;
                    finish();
                    exibirMensagem("Produto salvo com sucesso");
                } else {
                    exibirMensagem("Digite um preço para o produto");
                }
            } else {
                exibirMensagem("Digite uma descrição para o produto");
            }
        } else {
            exibirMensagem("Digite um nome para o produto");
        }

    }

    public void exibirMensagem (String texto){
        Toast.makeText( this,
                texto,
                Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {
        edtNomeProduto = findViewById(R.id.edtNomeProduto);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtProdutoPreco = findViewById(R.id.edtProdutoPreco);
    }
}