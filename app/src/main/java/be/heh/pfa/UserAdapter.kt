package be.heh.pfa

import android.content.DialogInterface
import android.util.Log
import be.heh.pfa.db.UserRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.db.MyDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



public class UserAdapter(private val users: MutableList<UserRecord>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: ImageView = itemView.findViewById(R.id.iv_userIcon_userCardViewItem)
        val userEmailTextView: TextView = itemView.findViewById(R.id.tv_userEmail_userCardViewItem)
        val permissionSpinner: Spinner = itemView.findViewById(R.id.spin_userPermission_userCardViewItem)
        val DeleteUserImageView: ImageView = itemView.findViewById(R.id.iv_deleteUser_userCardViewItem)
        val isActiveImageView: ImageView = itemView.findViewById(R.id.iv_isActive_userCardViewItem)
    }



        //onCreateViewHolder crée la vue qui va afficher les items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView = LayoutInflater.from(parent.context).inflate(R.layout.user_cardview_item, parent, false)
        return UserViewHolder(userView)
    }

        //onBindViewHolder : binder la liste des items avec les widgets (imageView, TextView etc)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            var currentUser = users[position]
            holder.userEmailTextView.text = currentUser.email
            if (currentUser.isActive) {
                holder.isActiveImageView.setImageResource(R.drawable.active)
                if (!currentUser.canWrite) {
                    holder.permissionSpinner.setSelection(0)  // position 0 du tableau = R/W
                } else {
                    holder.permissionSpinner.setSelection(1)  // position 1 du tableau = R
                }
            } else {
                holder.isActiveImageView.setImageResource(R.drawable.inactive)
                holder.permissionSpinner.setSelection(2)  // position 2 du tableau = compte bloqué
            }


            holder.DeleteUserImageView.setOnClickListener {
                //Gérer le clic sur l'cione de poubelle
                Log.i(
                    "Cardview btn Click",
                    "click sur le bouton edit de la cardview de : " + holder.userEmailTextView.text.toString()
                )
                AlertDialog.Builder(holder.itemView.context)
                // Titre et message de l'AlertDialog
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment Suprimer cet utilisateur ?")
                // Bouton "Confirmer"
                .setPositiveButton("Confirmer") { dialog: DialogInterface?, which: Int ->
                    // Code exécuté lorsque l'utilisateur clique sur "Confirmer"
                    Log.i("AlertDialog", "Confirmation")
                    GlobalScope.launch(Dispatchers.IO) {
                        val db = Room.databaseBuilder(
                            holder.itemView.context,
                            MyDb::class.java,
                            "MyDataBase"
                        ).build()
                        val dao = db.userDao()
                        val userInfo: UserRecord =
                            UserRecord(
                                currentUser.id,
                                currentUser.email,
                                currentUser.password,
                                currentUser.canWrite,
                                currentUser.isActive
                            )
                        dao.deleteUser(userInfo)
                    }
                    // Mettre à jour l'affichage
                    users.removeAt(position)
                    notifyItemRemoved(position)
                }
                // Bouton "Annuler"
                    .setNegativeButton("Annuler") { dialog: DialogInterface?, which: Int ->
                    // Code exécuté lorsque l'utilisateur clique sur "Annuler" (facultatif)
                    Log.i("AlertDialog", "Annulation")
                }
                // Afficher l'AlertDialog
                .show()



              /*  AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Options")
                    .setItems(R.array.permissions_array) { dialog, which ->
                        when (which) {
                            0 -> {
                                // Code à exécuter lorsque l'option "Modifier" est sélectionnée
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Option Modifier sélectionnée",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            1 -> {
                                // Code à exécuter lorsque l'option "Supprimer" est sélectionnée
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Option Supprimer sélectionnée",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .show()
                */
            }


            holder.permissionSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        // Gérer l'événement de sélection
                        val selectedPermission = parent?.getItemAtPosition(position).toString()
                        Log.i("Spinner", "Permission sélectionnée : $selectedPermission")

                        // Exécutez le code en fonction du choix
                        when (selectedPermission) {
                            "R" -> {
                                // Code à exécuter lorsque l'option "R" est sélectionnée
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Option R sélectionnée",
                                    Toast.LENGTH_SHORT
                                ).show()
                                currentUser.canWrite = false
                                currentUser.isActive = true
                                holder.isActiveImageView.setImageResource(R.drawable.active)

                            }

                            "R/W" -> {
                                // Code à exécuter lorsque l'option "R/W" est sélectionnée
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Option R/W sélectionnée",
                                    Toast.LENGTH_SHORT
                                ).show()
                                currentUser.canWrite = true
                                currentUser.isActive = true
                                holder.isActiveImageView.setImageResource(R.drawable.active)
                            }

                            "Bloqué" -> {
                                // Code à exécuter lorsque l'option "Compte bloqué" est sélectionnée
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Compte bloqué ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                currentUser.isActive = false
                                holder.isActiveImageView.setImageResource(R.drawable.inactive)
                            }
                        }
                        //updateUser(currentUser)
                        GlobalScope.launch(Dispatchers.IO) {
                            val db = Room.databaseBuilder(
                                holder.itemView.context,
                                MyDb::class.java,
                                "MyDataBase"
                            ).build()
                            val dao = db.userDao()
                            val userInfo: UserRecord =
                                UserRecord(
                                    currentUser.id,
                                    currentUser.email,
                                    currentUser.password,
                                    currentUser.canWrite,
                                    currentUser.isActive,
                                    currentUser.isSuperAdmin
                                )
                            dao.updateUser(userInfo)
                        }
                    }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                             //Gérer l'événement lorsque rien n'est sélectionné (si nécessaire)
                        }


                    }
                }

            /*private fun updateUser(user: UserRecord) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(applicationContext, MyDB::class.java, "MyDataBase").build()
            val dao = db.userDao()
            val userInfo: UserRecord =
                UserRecord(user.id, user.email, user.password, user.canWrite, user.isActive)
            dao.updateUser(userInfo)
        }
    }*/





    //retourne le nombre d'item présent dans la liste
    override fun getItemCount(): Int {
        return users.size
    }



}
