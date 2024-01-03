package be.heh.pfa.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import be.heh.pfa.R
import be.heh.pfa.db.MyDb
import be.heh.pfa.model.AuthenticatedUser
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {


    private var isBorrowed: Boolean = false
    private lateinit var db: MyDb

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = MyDb.getInstance(requireContext())

        // Setup button1
        val borrowBtn: Button = view.findViewById(R.id.btn_borrow_homeFragment)
        borrowBtn.setOnClickListener {
            borrowDevice()
        }

        // Setup button2
        val givebackBtn: Button = view.findViewById(R.id.btn_giveBack_homeFragment)
        givebackBtn.setOnClickListener {
            giveBackDevice()
        }

        val logoutBtn: Button = view.findViewById(R.id.btn_logout_homeFragment)
        logoutBtn.setOnClickListener {
            AuthenticatedUser.clearAuthenticatedUser()
            requireActivity().finish()
        }
    }


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
                val resultScan = result.contents.split(",")
                val scannedDeviceRefNumber = resultScan[3]
                checkDatabase(scannedDeviceRefNumber, isBorrowed)
            }
        }
    }

    private fun checkDatabase(scannedRefNumber: String, isBorrowed: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            val dao = db.deviceDao()
            var device = withContext(Dispatchers.Default) {
                dao.getDeviceByReferenceNumber(scannedRefNumber)
            }

            if (device != null) {
                if (device.isBorrowed == isBorrowed) {
                    if (device.isBorrowed) {
                        // matériel déja emprunté
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Le device est déjà emprunté", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // matériel déja remis
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Le device est déja remis dans l'inventaire", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    if (!device.isBorrowed) {
                        //matériel dispo pour l'emprunt
                        device.isBorrowed = true
                        dao.updateDevice(device)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Vous avez emprunté " + device.brand + " " + device.model, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        //matériel dispo pour le retour
                        device.isBorrowed = false
                        dao.updateDevice(device)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Vous avez remis " + device.brand + " " + device.model + " dans l'inventaire", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // matériel pas trouvé
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Aucune correspondance avec la base de donnée", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

