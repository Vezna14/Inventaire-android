package be.heh.pfa.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.R
import be.heh.pfa.UserAdapter
import be.heh.pfa.db.MyDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Récupérer la RecyclerView par son ID
        userRecyclerView = view.findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialiser l'adaptateur et le passer à la RecyclerView
        userAdapter = UserAdapter(emptyList())
        userRecyclerView.adapter = userAdapter

        // Charger les utilisateurs depuis la base de données et mettre à jour l'adaptateur
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(requireContext(), MyDB::class.java, "MyDataBase").build()
            val dao = db.userDao()
            val usersList = dao.getAllUsers()

            if (usersList != null) {
                withContext(Dispatchers.Main) {
                    userAdapter = UserAdapter(usersList)
                    userRecyclerView.adapter = userAdapter
                }
            }
        }

        return view
    }
}