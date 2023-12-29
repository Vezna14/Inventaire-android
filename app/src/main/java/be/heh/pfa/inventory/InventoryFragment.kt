package be.heh.pfa.inventory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.DeviceAdapter
import be.heh.pfa.R
import be.heh.pfa.db.DeviceRecord
import be.heh.pfa.db.MyDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class InventoryFragment : Fragment() {
    private lateinit var deviceRecyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)

        // Récupérer la RecyclerView par son ID
        deviceRecyclerView = view.findViewById(R.id.deviceRecyclerView)
        deviceRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialiser l'adaptateur et le passer à la RecyclerView
        deviceAdapter = DeviceAdapter(mutableListOf())
        deviceRecyclerView.adapter = deviceAdapter


       // Charger les devices depuis la base de données et mettre à jour l'adaptateur
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(requireContext(), MyDb::class.java, "MyDataBase").build()
            val dao = db.deviceDao()

           /* // Exemple d'insertion pré-population
            val device1 = DeviceRecord(
                type = "Mobile",
                brand = "Samsung",
                model = "Galaxy S21",
                referenceNumber = "S21-123",
                manufacturerWebsite = "https://www.samsung.com",
                qrCode = "QR123",
                isBorrowed = false
            )
            val device2 = DeviceRecord(
                type = "Tablet",
                brand = "Apple",
                model = "iPhone 12",
                referenceNumber = "IP12-123",
                manufacturerWebsite = "https://www.apple.com",
                qrCode = "QR456",
                isBorrowed = false
            )

            dao.insertDevice(device1)
            dao.insertDevice(device2)*/

            val devicesList = dao.getAllDevices().toMutableList()
            Log.i("AAAAAAAAAAAAAAAAAAAAAA", "est-ce qu'il y a des devices dans la db?")
            if (devicesList.isNotEmpty()) {
                Log.i("AAAAAAAAAAAAAAAAAAAAAA", "il y des devices dans la db")
                withContext(Dispatchers.Main) {
                    deviceAdapter = DeviceAdapter(devicesList)
                    deviceRecyclerView.adapter = deviceAdapter
                }
            }
        }

        return view
    }
}