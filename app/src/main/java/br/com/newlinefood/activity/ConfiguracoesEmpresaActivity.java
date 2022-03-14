package br.com.newlinefood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import br.com.newlinefood.R;
import br.com.newlinefood.helper.ConfiguracaoFirebase;
import br.com.newlinefood.helper.UsuarioFirebase;
import br.com.newlinefood.model.Empresa;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private Button buttonSalvar;
    private EditText edtNomeEmpresa, edtCategoria, edtPrazoEntrega, edtTaxaEntrega;
    private CircleImageView profile_image;

    private static final int SELECAO_GALERA = 200;

    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        databaseReference = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERA);
                }
            }
        });

        recuperarDadosEmpresa();
    }

    private void recuperarDadosEmpresa() {

        DatabaseReference empresaRef = databaseReference.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    edtNomeEmpresa.setText(empresa.getNome());
                    edtCategoria.setText(empresa.getCategoria());
                    edtTaxaEntrega.setText(empresa.getPrecoEntrega().toString());
                    edtPrazoEntrega.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImagem();
                    if (urlImagemSelecionada != ""){
                        Picasso.get().load(urlImagemSelecionada)
                                .into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosEmpresa (View view){
        String nome = edtNomeEmpresa.getText().toString();
        String taxa = edtTaxaEntrega.getText().toString();
        String categoria = edtCategoria.getText().toString();
        String tempo = edtPrazoEntrega.getText().toString();

        if (!nome.isEmpty()){
            if (!taxa.isEmpty()){
                if (!categoria.isEmpty()){
                    if (!tempo.isEmpty()){
                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setPrecoEntrega(Double.parseDouble(taxa));
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setUrlImagem(urlImagemSelecionada);
                        empresa.salvar();
                        finish();
                    } else {
                        exibirMensagem("Digite um tempo de entrega");
                    }
                } else {
                    exibirMensagem("Digite uma categoria para empresa");
                }
            } else {
                exibirMensagem("Digite uma taxa de entrega para empresa");
            }
        } else {
            exibirMensagem("Digite um nome para empresa");
        }

    }

    public void exibirMensagem (String texto){
        Toast.makeText( this,
                texto,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_GALERA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(getContentResolver(), localImagem);
                        break;
                }

                if (imagem != null){
                    profile_image.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte [] dadosImagem = baos.toByteArray();

                    StorageReference imagemReferencia = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemReferencia.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {
        buttonSalvar = findViewById(R.id.buttonSalvar);
        edtNomeEmpresa = findViewById(R.id.edtNomeEmpresa);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtPrazoEntrega = findViewById(R.id.edtPrazoEntrega);
        edtTaxaEntrega = findViewById(R.id.edtTaxaEntrega);
        profile_image = findViewById(R.id.profile_image);
    }
}