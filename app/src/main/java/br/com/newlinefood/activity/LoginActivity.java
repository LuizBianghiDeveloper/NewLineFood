package br.com.newlinefood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.com.newlinefood.R;
import br.com.newlinefood.helper.ConfiguracaoFirebase;
import br.com.newlinefood.helper.UsuarioFirebase;

public class LoginActivity extends AppCompatActivity {

    private Button btnAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsario;
    private LinearLayout linearTipoUsario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        inicializarComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (tipoAcesso.isChecked()){
                    linearTipoUsario.setVisibility(View.VISIBLE);
                } else {
                    linearTipoUsario.setVisibility(View.GONE);
                }
            }
        });

        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()){
                    if (!senha.isEmpty()){
                        if (tipoAcesso.isChecked()){
                            autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this,
                                                "Cadastro realizado com sucesso",
                                                Toast.LENGTH_SHORT).show();
                                        String usuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipousuario(usuario);
                                        abrirTelaPrincipal(usuario);
                                    } else {
                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite uma senha mais forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Por favor, digite um email válido!";
                                        } catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Essa conta já foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(LoginActivity.this,
                                                "Erro: " + erroExcecao,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUser = task.getResult().getUser().getDisplayName();
                                        abrirTelaPrincipal(tipoUser);
                                    }else {
                                        Toast.makeText(LoginActivity.this,
                                                "Erro ao fazer o login: " + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else {
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha o Email!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal(String tipoUsuario) {
       if (tipoUsuario.equals("E")){
           startActivity(new Intent(getApplicationContext(), EmpresaActivity.class ));
       } else {
           startActivity(new Intent(getApplicationContext(), HomeActivity.class ));
       }
    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            String tipoUser = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUser);
        }
    }

    private String getTipoUsuario(){
        return tipoUsario.isChecked() ? "E" : "U";
    }

    private void inicializarComponentes() {
        campoEmail = findViewById(R.id.edtCadastroEmail);
        campoSenha = findViewById(R.id.edtCadastroSenha);
        btnAcessar = findViewById(R.id.buttonAcessar);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsario = findViewById(R.id.linearTipoUsuario);
    }
}