package br.com.newlinefood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.newlinefood.R;
import br.com.newlinefood.helper.ConfiguracaoFirebase;
import br.com.newlinefood.helper.UsuarioFirebase;
import br.com.newlinefood.model.CEP;
import br.com.newlinefood.model.Empresa;
import br.com.newlinefood.model.Usuario;
import br.com.newlinefood.service.RetrofitConfig;
import br.com.newlinefood.util.MaskEditUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private Button buttonSalvar;
    private EditText edtEndereco, edtNome, edtCEP, edtNumero, edtBairro, edtData;

    private String idUsuarioLogado;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        edtCEP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 8) {
                    Call<CEP> call = new RetrofitConfig().getCEPService().buscarCEP(edtCEP.getText().toString());
                    call.enqueue(new Callback<CEP>() {
                        @Override
                        public void onResponse(Call<CEP> call, Response<CEP> response) {
                            CEP cep = response.body();
                            edtEndereco.setText(cep.getLogradouro());
                            edtBairro.setText(cep.getBairro());
                            edtCEP.setText(cep.getCep());
                        }

                        @Override
                        public void onFailure(Call<CEP> call, Throwable t) {
                            Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
                        }
                    });
                }
            }
        });


        edtData.addTextChangedListener(MaskEditUtil.mask(edtData, MaskEditUtil.FORMAT_DATE));

        databaseReference = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario() {

        DatabaseReference empresaRef = databaseReference.child("usuarios").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    edtNome.setText(usuario.getNome());
                    edtEndereco.setText(usuario.getEndereco());
                    edtBairro.setText(usuario.getBairro());
                    edtCEP.setText(usuario.getCep());
                    edtNumero.setText(usuario.getNumero());
                    edtData.setText(usuario.getData_nascimento());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosUsuario(View view) {
        String nome = edtNome.getText().toString();
        String endereco = edtEndereco.getText().toString();
        String cep = edtCEP.getText().toString();
        String numero = edtNumero.getText().toString();
        String bairro = edtBairro.getText().toString();
        String data = edtData.getText().toString();

        if (!nome.isEmpty()) {
            if (!cep.isEmpty()) {
                if (!numero.isEmpty()) {
                    if (!bairro.isEmpty()) {
                        if (!data.isEmpty()) {
                            if (!endereco.isEmpty()) {
                                Usuario usuario = new Usuario();
                                usuario.setIdUsuario(idUsuarioLogado);
                                usuario.setNome(nome);
                                usuario.setEndereco(endereco);
                                usuario.setBairro(bairro);
                                usuario.setCep(cep);
                                usuario.setNumero(numero);
                                usuario.setData_nascimento(data);
                                usuario.salvar();
                                exibirMensagem("Dados salvo com sucesso!");
                                finish();
                            } else {
                                exibirMensagem("Digite seu endereço completo!");
                            }
                        } else {
                            exibirMensagem("Digite a data de nascimento!");
                        }
                    } else {
                        exibirMensagem("Digite seu bairro!");
                    }
                } else {
                    exibirMensagem("Digite o numero");
                }
            } else {
                exibirMensagem("Digite o cep");
            }
        } else {
            exibirMensagem("Digite seu nome!");
        }

    }

    public void exibirMensagem(String texto) {
        Toast.makeText(this,
                texto,
                Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {
        buttonSalvar = findViewById(R.id.buttonSalvar);
        edtEndereco = findViewById(R.id.edtEndereco);
        edtNome = findViewById(R.id.edtNome);
        edtCEP = findViewById(R.id.edtCEP);
        edtNumero = findViewById(R.id.edtNumero);
        edtBairro = findViewById(R.id.edtBairro);
        edtData = findViewById(R.id.edtData);
    }
}