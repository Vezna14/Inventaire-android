package be.heh.pfa.profile

import be.heh.pfa.model.AuthenticatedUser
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import be.heh.pfa.R
import kotlinx.android.synthetic.main.activity_login.et_email_loginActivity
import kotlinx.android.synthetic.main.fragment_user_profile.*

class UserProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_email_userProfileFragment.text = "Email : " + AuthenticatedUser.email
        //tv_password_userProfileFragment.text = "Mot de passe : " + AuthenticatedUser.password
        tv_canWrite_userProfileFragment.text =
            "Peut modifier matériel : " + AuthenticatedUser.canWrite.toString()
        tv_isActive_userProfileFragment.text =
            "Est actif : " + AuthenticatedUser.isActive.toString()
    }


}