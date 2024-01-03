package be.heh.pfa.inventory

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.heh.pfa.DeviceAdapter
import be.heh.pfa.R
import be.heh.pfa.db.DeviceRecord
import be.heh.pfa.db.MyDb
import be.heh.pfa.AuthenticatedUser
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryFragment : Fragment() {
    private lateinit var deviceRecyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var db: MyDb
    var devicesList = mutableListOf<DeviceRecord>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)
        db = MyDb.getInstance(requireContext())

        // Récupérer la RecyclerView par son ID
        deviceRecyclerView = view.findViewById(R.id.deviceRecyclerView)
        deviceRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialiser l'adaptateur et le passer à la RecyclerView
        deviceAdapter = DeviceAdapter(mutableListOf())
        deviceRecyclerView.adapter = deviceAdapter

        // Charger les devices depuis la base de données et mettre à jour l'adaptateur
        GlobalScope.launch(Dispatchers.IO) {
            val dao = db.deviceDao()
            devicesList = dao.getAllDevices().toMutableList()
            if (devicesList.isNotEmpty()) {
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
        if (AuthenticatedUser.canWrite) {
            AddDeviceImageView.visibility = View.VISIBLE

        } else {
            AddDeviceImageView.visibility = View.GONE

        }
        AddDeviceImageView.setOnClickListener {
            AddNewdevice()
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
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // Handle the scanned QR code
                val resultScan = result.contents.split(",")
                //0: type, 1: brand, 2: model, 3: referenceNumber, 4: manufacturerWebsite
                try {
                    if (resultScan.size == 5 &&
                        (resultScan[0] == "Smartphone" || resultScan[0] == "Tablette") &&
                        resultScan.all { it.isNotBlank() && it.isNotEmpty() }) {

                        println("La variable splittée satisfait toutes les conditions.")
                        val scannedDevice = DeviceRecord(
                            type = resultScan[0],
                            brand = resultScan[1],
                            model = resultScan[2],
                            referenceNumber = resultScan[3],
                            manufacturerWebsite = resultScan[4],
                            isBorrowed = false
                        )

                        AddDeviceInDB(scannedDevice)


                    } else {
                        println("La variable splittée ne satisfait pas toutes les conditions.")
                        Toast.makeText(
                            requireContext(),
                            "Erreur : données invalides\n",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                        catch(e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Erreur lors de la lecture du QR code\n",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }
        }

    // Regex pour check si une chaine de carac est un site internet valide

    fun AddDeviceInDB(scannedDevice: DeviceRecord) {
        GlobalScope.launch(Dispatchers.IO) {
            val dao = db.deviceDao()
            if (dao.isReferenceNumberAlreadyUsed(scannedDevice.referenceNumber.toString())) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ajout inpossimble :\nNuméro de référence déja utilisé", Toast.LENGTH_SHORT).show()
                }
            } else {
                dao.insertDevice(scannedDevice)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "${scannedDevice.brand} ${scannedDevice.model} \na été ajouté à l'inventaire", Toast.LENGTH_SHORT).show()
                    devicesList.add(scannedDevice)
                    deviceAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}


