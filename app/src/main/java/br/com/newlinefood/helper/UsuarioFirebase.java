package br.com.newlinefood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static String getIdUsuario(){
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return auth.getCurrentUser().getUid();
    }

    public static FirebaseUser firebaseUser(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarTipousuario (String tipo){
        try {
            FirebaseUser user = firebaseUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(tipo).build();
            user.updateProfile(profile);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

}
