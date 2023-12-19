package be.heh.pfa

import android.util.Log
import be.heh.pfa.db.UserRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

public class UserAdapter(private val users: List<UserRecord>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: ImageView = itemView.findViewById(R.id.iv_userIcon_userCardViewItem)
        val userEmailTextView: TextView = itemView.findViewById(R.id.tv_userEmail_userCardViewItem)
        val permissionEditText: EditText = itemView.findViewById(R.id.et_userPermission_userCardViewItem)
        val applyButton: Button = itemView.findViewById(R.id.bt_editUser_userCardViewItem)
    }


        //onCreateViewHolder crée la vue qui va afficher les items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView = LayoutInflater.from(parent.context).inflate(R.layout.user_cardview_item, parent, false)
        return UserViewHolder(userView)
    }

        //onBindViewHolder : binder la liste des items avec les widgets (imageView, TextView etc)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.userEmailTextView.text = currentUser.email
        holder.permissionEditText.setText(currentUser.canWrite.toString())
        holder.applyButton.setOnClickListener{
            //Gérer le clic sur le bouton "Apply change ici
            Log.i("Cardview btn Click", "click sur le bouton edit de la cardview de : "+holder.userEmailTextView.text.toString())
        }
    }

    //retourne le nombre d'item présent dans la liste
    override fun getItemCount(): Int {
        return users.size
    }
}
