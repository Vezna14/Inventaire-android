package be.heh.pfa.inventory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.DeviceAdapter
import be.heh.pfa.R
import be.heh.pfa.db.DeviceRecord
import be.heh.pfa.db.MyDb
import be.heh.pfa.model.AuthenticatedUser
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.fragment_inventory.iv_addDevice_fragmentInventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            // Exemple d'insertion pré-population
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
            dao.insertDevice(device2)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var AddDeviceImageView: ImageView = view.findViewById(R.id.iv_addDevice_fragmentInventory)
        if(AuthenticatedUser.canWrite){
            AddDeviceImageView.visibility = View.VISIBLE

        }
        else{
            AddDeviceImageView.visibility = View.GONE

        }
        AddDeviceImageView.setOnClickListener {
            AddNewdevice()
        }



    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(requireContext(), MyDb::class.java, "MyDataBase").build()
            val dao = db.deviceDao()

            val devicesList = dao.getAllDevices().toMutableList()
            Log.i("AAAAAAAAAAAAAAAAAAAAAA", "est-ce qu'il y a des devices dans la db?")
            if (devicesList.isNotEmpty()) {
                Log.i("AAAAAAAAAAAAAAAAAAAAAA", "il y des devices dans la db")
                withContext(Dispatchers.Main) {
                    deviceAdapter = DeviceAdapter(devicesList)

                }
            }
        }
    }


    private fun AddNewdevice() {
        scanQRCode()
    }

    private fun scanQRCode() {
        IntentIntegrator.forSupportFragment(this@InventoryFragment)
            .setCaptureActivity(CaptureActivity::class.java)
            .setOrientationLocked(false)
            .initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("----------------------------", "onActivityResult: Déclenchement de OnActivityResult")
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val donnees = data?.getParcelableExtra<DeviceRecord>("updatedDevice")
            val position= data?.getIntExtra("position", 0)
            // Faites quelque chose avec les données récupérées
            deviceAdapter.updateRecyclerViewWithUpdatedDevice(donnees!!, position!!)
            Log.i("--------------------------", "onActivityResult: Données reçues : $donnees")
        }
        else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents != null) {
                    // Handle the scanned QR code
                    val resultScan = result.contents.split(",")
                    //0: type, 1: brand, 2: model, 3: referenceNumber, 4: manufacturerWebsite
                    try {
                        val scannedDevice = DeviceRecord(
                            type = resultScan[0],
                            brand = resultScan[1],
                            model = resultScan[2],
                            referenceNumber = resultScan[3],
                            manufacturerWebsite = resultScan[4],
                            qrCode = "",
                            isBorrowed = false
                        )
                        AddDeviceInDB(scannedDevice)
                    } catch (e: Exception) {
                        Log.i("AAAAAAAAAAAAAAAAAAAAAA", "erreur lors de la lecture du QR code")
                        Toast.makeText(
                            requireContext(),
                            "Erreur lors de la lecture du QR code\nInfo non valide",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun AddDeviceInDB(scannedDevice: DeviceRecord) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(requireContext(), MyDb::class.java, "MyDataBase").build()
            val dao = db.deviceDao()
            if (dao.isReferenceNumberAlreadyUsed(scannedDevice.referenceNumber.toString())) {
                Log.i(
                    "AAAAAAAAAAAAAAAAAAAAAA",
                    "numéro de référence déjà utilisé, produit déja dans la DB"
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Ajout inpossimble :\nNuméro de référence déja utilisé",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Log.i(
                    "AAAAAAAAAAAAAAAAAAAAAA",
                    "numéro de réfénrece pas utilisé, produit pas encore dans la DB"
                )
                dao.insertDevice(scannedDevice)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "${scannedDevice.brand} ${scannedDevice.model} \na été ajouté à l'inventaire",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }


}


