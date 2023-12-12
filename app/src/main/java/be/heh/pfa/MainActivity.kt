package be.heh.pfa

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

//importer les éléments du layout activity main
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun mainLayoutClickEvent(v: View) {
        when (v.id) {
            btn_login_mainActivity.id -> checkCredential()
            btn_createAccount_mainActivity.id -> goToRegisterActivity()
        }
    }

    private fun checkCredential() {
        /*AsyncTask.execute {
            val db = Room.databaseBuilder(applicationContext,MyDB::class.java, "MyDataBase").build()
            val dao = db.userDao()
            val dbL = dao?.getUserByEmailAndPassword(email,password)
            var uL = User(
                dbL?.id ?: 0,dbL?.email ?: "INDEFINI",dbL?.password ?: "INDEFINI",dbL?.canWrite ?: false
            )
            println(uL.toString())*/
        Toast.makeText(this, "appui sur le btn login", Toast.LENGTH_SHORT).show()

    }

    private fun goToRegisterActivity(){
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }




}



