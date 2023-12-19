package be.heh.pfa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.R
import be.heh.pfa.db.MyDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PageActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        // récup la recyclerView par son ID
        val userRecyclerView: RecyclerView= findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialiser l'adaptateur et le passer à la RecyclerView
        userAdapter = UserAdapter(emptyList())
        userRecyclerView.adapter = userAdapter

        // Charger les utilisateurs depuis la base de données et mettre à jour l'adaptateur
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(applicationContext, MyDB::class.java, "MyDataBase").build()
            val dao = db.userDao()
            val usersList = dao.getAllUsers()

            if (usersList != null) {
                withContext(Dispatchers.Main) {
                    userAdapter = UserAdapter(usersList)
                    userRecyclerView.adapter = userAdapter
                }
            }
        }
    }
}
