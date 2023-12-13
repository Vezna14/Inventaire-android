package be.heh.pfa

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import be.heh.pfa.db.MyDB
import be.heh.pfa.db.UserRecord
import be.heh.pfa.model.User
import kotlinx.android.synthetic.main.activity_main.btn_createAccount_mainActivity
import kotlinx.android.synthetic.main.activity_main.btn_login_mainActivity
import kotlinx.android.synthetic.main.activity_main.et_password_mainActivity

//importer les éléments du layout activity register
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }


     fun RegisterLayoutClickEvent(v: View) {
        when (v.id) {
            btn_register_registerActivity.id -> registerNewUser()

        }
    }
    fun isAlphaNumericAndSpecialChar(input: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-Z0-9!@#\$%^&*()-_+=<>?/{}|:]*$")
        return pattern.matcher(input).matches()
    }
    private fun validateInput(): Boolean {
        //vérification des champs mail mdp et confirmation mdp
        var email =et_email_registerActivity.text.toString()
        var mdp= et_password_registerActivity.text.toString()
        var confmdp = et_confirmPassword_registerActivity.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez entrer une adresse e-mail valide.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isAlphaNumericAndSpecialChar(mdp)){
            Toast.makeText(this, "Le mot de passe ne peut contenir que des caractères alphanumérique (pas d'espace).", Toast.LENGTH_SHORT).show()
        }

        if (mdp.length < 4) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 4 caractères.", Toast.LENGTH_SHORT).show()
            return false
        }


        if (confmdp != mdp) {
            Toast.makeText(this, "La confirmation du mot de passe ne correspond pas.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Tous les champs sont valides
        return true
    }

    //if (password.contains(" "){print("attention , mdp en un seul mot uniquement")}
    private fun registerNewUser() {
        if (validateInput()) {
            val user = User(0, et_email_registerActivity.text.toString(), et_password_registerActivity.text.toString())
            // Utilisation de coroutines pour effectuer l'insertion de manière asynchrone
            GlobalScope.launch(Dispatchers.IO) {
                val db = Room.databaseBuilder(applicationContext, MyDB::class.java, "MyDataBase").build()
                val dao = db.userDao()

                if (dao.getUserByEmail(user.email) != null) {
                    // Adresse email déjà utilisée
                    // Utilisation de withContext pour passer du contexte IO au contexte principal (UI)
                    //withContext(Dispatchers.Main){} --> executer du code dans le THREAD UI
                    withContext(Dispatchers.Main) {
                        Log.i("RegisterActivity", "Adresse email déjà utilisée")
                        Toast.makeText(applicationContext,"Adresse e-mail déjà utilisée",Toast.LENGTH_SHORT).show()
                    }
                } else {

                    dao.insertUser(UserRecord(user.id, user.email, user.password, user.canWrite))
                    //withContext(Dispatchers.Main){} --> executer du code dans le THREAD UI
                    withContext(Dispatchers.Main) {Toast.makeText(applicationContext, "Utilisateur enregistré avec succès", Toast.LENGTH_SHORT).show()}
                    val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

            et_password_registerActivity.text.clear()
            et_confirmPassword_registerActivity.text.clear()
    }
}







