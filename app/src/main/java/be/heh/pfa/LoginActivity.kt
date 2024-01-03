package be.heh.pfa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import be.heh.pfa.db.MyDb
import be.heh.pfa.model.AuthenticatedUser
import be.heh.pfa.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//importer les éléments du layout activity main
import kotlinx.android.synthetic.main.activity_login.*
//coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var db: MyDb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = MyDb.getInstance(applicationContext)

    }

    override fun onStart() {
        super.onStart()
        //vérif si 1er lancement de l'appli
        /*val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = sharedPrefs.getBoolean("firstStart", true)

        if (firstStart) {
            showCreateAdminDialog()
        }*/
        GlobalScope.launch(Dispatchers.IO) {
            //val db = Room.databaseBuilder(applicationContext, MyDb::class.java, "MyDataBase").build()
            val dao = db.userDao()
            val hasUser = dao.hasAtLeastOneUser()

            if (!hasUser) {
                // Aucun User dans la DB --> faut créer le Super User
                withContext(Dispatchers.Main) {
                    showCreateAdminDialog()
                }
            }
        }

    }

    //ne pas conserver le champ mdp de la vue login quand l'activité n'est plus visible à l'écran
    override fun onStop() {
        super.onStop()

        et_password_loginActivity.text.clear()

    }

    fun loginLayoutClickEvent(v: View) {
        when (v.id) {
            btn_login_loginActivity.id -> checkCredential()
            btn_createAccount_loginActivity.id -> goToRegisterActivity()
        }
    }

    private fun checkCredential() {
        var mail = et_email_loginActivity.text.toString()
        var mdp = et_password_loginActivity.text.toString()

        if (mail.isEmpty() || mdp.isEmpty()) {
            Toast.makeText(this, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show()
        } else {
            val user = User(0, mail, mdp)
            // Utilisation de coroutines pour effectuer la vérif avec la DB de manière asynchrone
            GlobalScope.launch(Dispatchers.IO) {
                // val db =Room.databaseBuilder(applicationContext, MyDb::class.java, "MyDataBase").build()
                val dao = db.userDao()
                //val dbl=dao.getAllUsers()
                var pretendedUser = dao.getUserByEmailAndPassword(user.email, user.password)
                if (pretendedUser != null) {
                    if (pretendedUser.isActive) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Connexion réussie " + pretendedUser.email,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        AuthenticatedUser.setAuthenticatedUserInfo(pretendedUser)
                        Log.i(
                            "BTN login------------------------",
                            "user isAdmin :" + AuthenticatedUser.isSuperAdmin.toString()
                        )
                        Log.i(
                            "BTN login------------------------",
                            "user email :" + AuthenticatedUser.email
                        )
                        Log.i(
                            "BTN login------------------------",
                            "user password :" + AuthenticatedUser.password
                        )
                        Log.i(
                            "BTN login------------------------",
                            "user isActive :" + AuthenticatedUser.isActive.toString()
                        )
                        Log.i(
                            "BTN login------------------------",
                            "user canWrite :" + AuthenticatedUser.canWrite.toString()
                        )
                        goToMainActivity()
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Accès refusé.\nCompte bloqué.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "identifiants incorrects",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun goToRegisterActivity(permission: Boolean = false) {

        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("permission", permission)
        startActivity(intent)

    }

    private fun goToMainActivity() {

        val intent = Intent(this@LoginActivity, MainActivity::class.java)

        startActivity(intent)

    }

    //pop up pour la création du Super Amdin
    private fun showCreateAdminDialog() {
        MaterialAlertDialogBuilder(this)
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



