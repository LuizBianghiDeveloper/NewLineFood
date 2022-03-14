package br.com.newlinefood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;
    private static StorageReference storageReference;



    //retorna a referencia do database
    public static DatabaseReference getFirebase(){
        if (databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance("https://newlinefood-default-rtdb.firebaseio.com").getReference();
        }
        return databaseReference;
    }

    //retorna a referencia do firebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    //retorna a referencia do storage
    public static StorageReference getFirebaseStorage(){
        if (storageReference == null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }
}
