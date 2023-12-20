package be.heh.pfa

import android.util.Log
import be.heh.pfa.db.UserRecord
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.db.MyDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



public class UserAdapter(private val users: List<UserRecord>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: ImageView = itemView.findViewById(R.id.iv_userIcon_userCardViewItem)
        val userEmailTextView: TextView = itemView.findViewById(R.id.tv_userEmail_userCardViewItem)
        val permissionSpinner: Spinner = itemView.findViewById(R.id.spin_userPermission_userCardViewItem)
        val editUserOptions: ImageView = itemView.findViewById(R.id.iv_editUser_userCardViewItem)
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


            holder.editUserOptions.setOnClickListener {
                //Gérer le clic sur les 3 petits points
                Log.i(
                    "Cardview btn Click",
                    "click sur le bouton edit de la cardview de : " + holder.userEmailTextView.text.toString()
                )
                val popupMenu = PopupMenu(holder.itemView.context, holder.editUserOptions)
                val inflater: MenuInflater = popupMenu.menuInflater
                inflater.inflate(R.menu.user_management_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.item_permission_menuManagement -> {
                            // Gérer l'action "changer les permissions
                            Log.i("PopupMenu", "permissions : ${holder.userEmailTextView.text}")
                            true
                        }

                        R.id.item_enableDisable_menuManagement -> {
                            // Gérer l'action "activer/désactiver"
                            Log.i(
                                "PopupMenu",
                                "activer/désactiver : ${holder.userEmailTextView.text}"
                            )
                            true
                        }

                        R.id.item_delete_menuManagement -> {
                            // Gérer l'action "Supprimer"
                            Log.i("PopupMenu", "Supprimer : ${holder.userEmailTextView.text}")
                            true
                        }

                        else -> false
                    }
                }

                popupMenu.show()

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
                                MyDB::class.java,
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
