package be.heh.pfa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import be.heh.pfa.db.MyDB
import be.heh.pfa.model.User

//importer les éléments du layout activity main
import kotlinx.android.synthetic.main.activity_login.*
//coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }

    override fun onStart() {
        super.onStart()
        //vérif si 1er lancement de l'appli
        /*val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = sharedPrefs.getBoolean("firstStart", true)

        if (firstStart) {
            showStartDialog()
        }*/
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(applicationContext, MyDB::class.java, "MyDataBase").build()
            val dao = db.userDao()
            val hasUser = dao.hasAtLeastOneUser()

            if (!hasUser) {
                // Aucun User dans la DB --> faut créer le Super User
                withContext(Dispatchers.Main) {
                    showStartDialog()
                }
            }
        }

    }

    //ne pas conserver le champ mdp de la vue login quand l'activité n'est plus visible à l'écran
    override fun onStop() {
        super.onStop()

        et_password_loginActivity.text.clear()

    }

    fun mainLayoutClickEvent(v: View) {
        when (v.id) {
            btn_login_loginActivity.id -> checkCredential()
            btn_createAccount_loginActivity.id -> goToRegisterActivity()
        }
    }

    private fun checkCredential() {
        var mail=et_email_loginActivity.text.toString()
        var mdp = et_password_loginActivity.text.toString()

        if (mail.isEmpty() || mdp.isEmpty()){
            Toast.makeText(this, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show()
        }
        else{
            val user = User(0, mail, mdp)
            // Utilisation de coroutines pour effectuer la vérif avec la DB de manière asynchrone
            GlobalScope.launch(Dispatchers.IO) {
                val db =Room.databaseBuilder(applicationContext, MyDB::class.java, "MyDataBase").build()
                val dao = db.userDao()
                //val dbl=dao.getAllUsers()
                if(dao.getUserByEmailAndPassword(user.email,user.password) != null){
                    val myLoggedUser=dao.getUserByEmailAndPassword(user.email,user.password)

                    goToPageActivity()
                    withContext(Dispatchers.Main){Toast.makeText(applicationContext,"mail : "+ user.email + "perm : "+ myLoggedUser?.canWrite ,Toast.LENGTH_SHORT).show()}
                }
                else{
                    withContext(Dispatchers.Main){Toast.makeText(applicationContext,"identifiants incorrectes",Toast.LENGTH_SHORT).show()}
                    }
                }
            }
        }

    private fun goToRegisterActivity(permission: Boolean = false){

        val intent = Intent(this,RegisterActivity::class.java)
        intent.putExtra("permission",permission)
        startActivity(intent)

    }

    private fun goToPageActivity(){

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)

    }

    //pop up pour la création du Super Amdin
    private fun showStartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Configuration du Super Admin")
            .setMessage("Il s'agit du premier démarrage de l'application.\nAppuyez sur OK pour configurer le Super Admin.")
            .setCancelable(false)
            .setPositiveButton("ok") { dialog, _ ->  //2eme paramètre (id bouton) pas utilisé --> on remplace par " _ " car " _ " = espace réservé pour un param pas utilisé.
                dialog.dismiss()
                goToRegisterActivity(true)
            }
            .create()
            .show()

       /* val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()*/
    }






}



