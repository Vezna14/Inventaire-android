package be.heh.pfa.inventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.heh.pfa.R
import be.heh.pfa.db.DeviceRecord
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import be.heh.pfa.db.MyDb
import kotlinx.android.synthetic.main.activity_device_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceDetailsActivity : AppCompatActivity() {

    var deviceId :Long = 0
    var deviceType :String = ""
    var deviceBrand :String = ""
    var deviceModel :String = ""
    var deviceReferenceNumber :String = ""
    var deviceManufacturerWebsite :String = ""
    var deviceQrCode :String = ""
    var deviceIsBorrowed : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)

        val device = intent.getParcelableExtra<DeviceRecord>("device")
        // Vérifier si l'objet n'est pas nul avant de l'utiliser
        if (device != null) {
            // Utilisez l'objet DeviceRecord ici
            deviceId = device.id
            deviceType = device.type.toString()
            deviceBrand = device.brand.toString()
            deviceModel = device.model.toString()
            deviceReferenceNumber = device.referenceNumber.toString()
            deviceManufacturerWebsite = device.manufacturerWebsite.toString()
            deviceQrCode = device.qrCode.toString()
            deviceIsBorrowed = device.isBorrowed

        } else {
            // Gestion du cas où l'objet DeviceRecord est nul
            Log.e("DeviceDetailsActivity", "L'objet DeviceRecord est nul.")
            // Peut-être afficher un message d'erreur ou revenir en arrière
            finish()
        }
        //appui sur le btn confirmer
        btn_confirmChanges_deviceDetailsActivity.setOnClickListener {
            confirmDialog()
        }
        if(deviceType == "Mobile"){
            iv_deviceIcon_deviceDetailsActivity.setImageResource(R.drawable.ic_smartphone)
        }
        else if(deviceType == "Tablette"){
            iv_deviceIcon_deviceDetailsActivity.setImageResource(R.drawable.ic_tablet)
        }

        tv_deviceId_deviceDetailsActivity.text = deviceId.toString()
        et_deviceType_deviceDetailsActivity.setText(deviceType)
        et_deviceBrand_deviceDetailsActivity.setText(deviceBrand)
        et_deviceModel_deviceDetailsActivity.setText(deviceModel)
        et_deviceRefNumber_deviceDetailsActivity.setText(deviceReferenceNumber)
        et_deviceWebsite_deviceDetailsActivity.setText(deviceManufacturerWebsite)
        et_deviceIsBorrowed_deviceDetailsActivity.setText(deviceQrCode)


    }

    fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Voulez-vous vraiment modifier ce matériel ?")
        builder.setPositiveButton("Oui") { dialog, which ->
            updateDevice()
            val intent = Intent(this, DeviceDetailsActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun updateDevice() {
        val id=deviceId
        val type = et_deviceType_deviceDetailsActivity.text.toString()
        val brand = et_deviceBrand_deviceDetailsActivity.text.toString()
        val model = et_deviceModel_deviceDetailsActivity.text.toString()
        val referenceNumber = et_deviceRefNumber_deviceDetailsActivity.text.toString()
        val manufacturerWebsite = et_deviceWebsite_deviceDetailsActivity.text.toString()
        val qrCode = et_deviceIsBorrowed_deviceDetailsActivity.text.toString()
        val isBorrowed = deviceIsBorrowed

        var deviceToUpdate = DeviceRecord(id, type, brand, model, referenceNumber, manufacturerWebsite, qrCode, isBorrowed)
       GlobalScope.launch(Dispatchers.IO) {
           val db = Room.databaseBuilder(applicationContext, MyDb::class.java, "MyDataBase").build()
           val dao = db.deviceDao()
           dao.updateDevice(deviceToUpdate)
           withContext(Dispatchers.Main) {
               Toast.makeText(applicationContext, "Matériel modifié", Toast.LENGTH_SHORT).show()
               finish()
           }
       }
    }
}