package be.heh.pfa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.btn_createAccount_mainActivity
import kotlinx.android.synthetic.main.activity_main.btn_login_mainActivity

//importer les éléments du layout activity register
import kotlinx.android.synthetic.main.activity_register.*

import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }



     fun RegisterLayoutClickEvent(v: View) {
        when (v.id) {
            btn_register_registerActivity.id -> registernewUser()

        }
    }
    fun isAlphaNumericAndSpecialChar(input: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-Z0-9!@#\$%^&*()-_+=<>?/{}|:]*$")
        return pattern.matcher(input).matches()
    }
    private fun validateInput(): Boolean {
        var email =et_email_registerActivity.text.toString()
        var mdp= et_password_registerActivity.text.toString()
        var confmdp = et_confirmPassword_registerActivity.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez entrer une adresse e-mail valide.", Toast.LENGTH_SHORT).show()
            return false
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
     private fun registernewUser(){
        Toast.makeText(this, "appui sur le btn register", Toast.LENGTH_SHORT).show()
        if (validateInput()){
            Toast.makeText(this, "les champs sont valides", Toast.LENGTH_SHORT).show()
        }



     }
}
