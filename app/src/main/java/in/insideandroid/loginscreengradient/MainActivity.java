package in.insideandroid.loginscreengradient;

import static in.insideandroid.loginscreengradient.SharedHelper.sha256;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText editText,input_password;
    Button button;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        input_password = (EditText) findViewById(R.id.input_password);
        button = (Button) findViewById(R.id.button);

        // Création de la base de données ou ouverture de connexion
        db = openOrCreateDatabase("ComptesWeb",MODE_PRIVATE,null);
        // Création de la table "users"
        db.execSQL("CREATE TABLE IF NOT EXISTS USERS (login varchar primary key, password varchar);");
        // si la table "users" est vide alors ajouter l'utilisateur admin avec mot de passe "123"
        SQLiteStatement s = db.compileStatement("select count(*) from users;");
        long c = s.simpleQueryForLong();
        if (c==0){
            db.execSQL("insert into users (login, password) values (?,?)", new String[] {"admin", sha256("123")});
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strLogin = editText.getText().toString();
                String strPwd = input_password.getText().toString();
                // Créer un curseur pour récupérer le résultat de la requête select
                Cursor cur = db.rawQuery("select password from users where login =?", new String[] {strLogin});
                try {
                    cur.moveToFirst();
                    String p = cur.getString(0);
                    if (p.equals(sha256(strPwd))){
                        Toast.makeText(getApplicationContext(),"Bienvenue " + strLogin, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(),NavigationDrawer.class);
                        startActivity(i);
                    }else{
                        editText.setText("");
                        input_password.setText("");
                        Toast.makeText(getApplicationContext(),"Echec de connexion",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    editText.setText("");
                    input_password.setText("");
                    Toast.makeText(getApplicationContext(),"Utilisateur Inexistant",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }
}
