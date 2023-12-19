package be.heh.pfa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import be.heh.pfa.home.HomeFragment
import be.heh.pfa.inventory.InventoryFragment
import be.heh.pfa.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        // Utilisez setOnItemSelectedListener pour détecter les changements de sélection
        bottomNavigationView.setOnItemSelectedListener(object :NavigationBarView.OnItemSelectedListener  {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_home -> {
                        replaceFragment(HomeFragment())
                        return true
                    }
                    R.id.navigation_inventory -> {
                        replaceFragment(InventoryFragment())
                        return true
                    }
                    R.id.navigation_profile -> {
                        replaceFragment(ProfileFragment())
                        return true
                    }
                }
                return false
            }
        })

        // Utilisez setOnItemReselectedListener pour détecter les éléments déjà sélectionnés
        bottomNavigationView.setOnItemReselectedListener(object : NavigationBarView.OnItemReselectedListener {
            override fun onNavigationItemReselected(item: MenuItem) {
                // Faire quelque chose si l'élément est déjà sélectionné
                //Todo remonter en haut de la vue si appui sur icone actuelle
            }
        })

        // Sélectionner le premier élément au lancement
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }

    // changer le fragment afficher dans l'activité
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}