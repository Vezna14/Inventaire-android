package be.heh.pfa.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room

import be.heh.pfa.R
import be.heh.pfa.db.MyDb
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {


    private var isBorrowed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup button1
        val button1: Button = view.findViewById(R.id.btn_borrow_homeFragment)
        button1.setOnClickListener {
            borrowDevice()
        }

        // Setup button2
        val button2: Button = view.findViewById(R.id.btn_giveBack_homeFragment)
        button2.setOnClickListener {
            giveBackDevice()
        }
    }
    /*fun homeLayoutClickEvent(v: View) {
        when (v.id) {
            btn_borrow_homeFragment.id -> borrowDevice()
            btn_giveBack_homeFragment.id -> giveBackDevice()
        }
    }*/

    fun borrowDevice() {
        isBorrowed = true
        scanQRCode()

    }

    fun giveBackDevice() {
        isBorrowed = false
        scanQRCode()
    }

    private fun scanQRCode() {
        IntentIntegrator.forSupportFragment(this@HomeFragment)
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
                val scannedId = result.contents
                checkDatabase(scannedId, isBorrowed)
            }
        }
    }

    private fun checkDatabase(scannedId: String, isBorrowed: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                requireContext(),
                MyDb::class.java, "MyDataBase"
            ).build()
            val dao = db.deviceDao()


            var device = withContext(Dispatchers.Default) {
                dao.getDeviceById(scannedId.toLong())
            }


                if (device != null) {
                    if(device.isBorrowed == isBorrowed) {
                        if (device.isBorrowed) {
                            // matériel déja emprunté
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Le device est déjà emprunté",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            // matériel déja remis
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Le device est déja remis dans l'inventaire",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    } else {
                        if (!device.isBorrowed) {
                            //matériel dispo pour l'emprunt
                            device.isBorrowed = true
                            dao.updateDevice(device)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Vous avez emprunté " + device.brand + " " + device.model,
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    }else{
                            //matériel dispo pour le retour
                            device.isBorrowed = false
                            dao.updateDevice(device)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Vous avez remis " + device.brand + " " + device.model + " dans l'inventaire",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    // device not found or doesn't match the criteria
                    // Handle accordingly
                    withContext(Dispatchers.Main) {Toast.makeText(requireContext(), "Aucune correspondance avec la base de donnée", Toast.LENGTH_SHORT).show()}
                }
            }
        }
    }

