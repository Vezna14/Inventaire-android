package be.heh.pfa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.room.Room
import be.heh.pfa.db.MyDB
import be.heh.pfa.model.User

//importer les éléments du layout activity main
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.et_email_registerActivity
import kotlinx.android.synthetic.main.activity_register.et_password_registerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStop() {
        super.onStop()
        //ne pas conserver le champ mdp de la vue login quand l'activité n'est plus visible à l'écran
        et_password_mainActivity.text.clear()

    }

    fun mainLayoutClickEvent(v: View) {
        when (v.id) {
            btn_login_mainActivity.id -> checkCredential()
            btn_createAccount_mainActivity.id -> goToRegisterActivity()
        }
    }

    private fun checkCredential() {
        var mail=et_email_mainActivity.text.toString()
        var mdp = et_password_mainActivity.text.toString()

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
                    goToPageActivity()

                }
                else{
                    withContext(Dispatchers.Main){Toast.makeText(applicationContext,"identifiants incorrectes",Toast.LENGTH_SHORT).show()}

                    }



        }





            //Toast.makeText(this, dbl.toString(), Toast.LENGTH_SHORT).show()
           //Log.i("MainActivity", dbl.toString())
            //val dbL = dao?.getUserByEmailAndPassword(email, password)
            /*var uL = User(
                dbL?.id ?: 0,
                dbL?.email ?: "INDEFINI",
                dbL?.password ?: "INDEFINI",
                dbL?.canWrite ?: false
            )*/

            //Toast.makeText(this, "appui sur le btn login", Toast.LENGTH_SHORT).show()

        }
    }

    private fun goToRegisterActivity(){

        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)

    }

    private fun goToPageActivity(){

        val intent = Intent(this@MainActivity, PageActivity::class.java)
        startActivity(intent)

    }




}



