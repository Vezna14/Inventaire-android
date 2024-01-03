package be.heh.pfa.inventory

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.heh.pfa.R
import be.heh.pfa.db.DeviceRecord
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import be.heh.pfa.DeviceAdapter
import be.heh.pfa.db.MyDb
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_device_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceDetailsActivity : AppCompatActivity() {

    var deviceId: Long = 0
    var deviceType: String = ""
    var deviceBrand: String = ""
    var deviceModel: String = ""
    var deviceReferenceNumber: String = ""
    var deviceManufacturerWebsite: String = ""
    var deviceQrCode: String = ""
    var deviceIsBorrowed: Boolean = false
    val position = 0

    private lateinit var db: MyDb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)

        db = MyDb.getInstance(applicationContext)
        val device = intent.getParcelableExtra<DeviceRecord>("device")
        val position = intent.getIntExtra("position", 0)

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
            //si l'objet DeviceRecord est nul
            Log.e("DeviceDetailsActivity", "L'objet DeviceRecord est nul.")
            // Peut-être afficher un message d'erreur ou revenir en arrière
            finish()
        }

        //appui sur le btn confirmer
        btn_confirmChanges_deviceDetailsActivity.setOnClickListener {
            confirmChangeDialog()
        }

        when (deviceType) {
            "Mobile" -> iv_deviceIcon_deviceDetailsActivity.setImageResource(R.drawable.ic_smartphone)
            "Tablette" -> iv_deviceIcon_deviceDetailsActivity.setImageResource(R.drawable.ic_tablet)

        }


        tv_deviceId_deviceDetailsActivity.text = deviceId.toString()
        et_deviceType_deviceDetailsActivity.setText(deviceType)
        et_deviceBrand_deviceDetailsActivity.setText(deviceBrand)
        et_deviceModel_deviceDetailsActivity.setText(deviceModel)
        tv_deviceRefNumber_deviceDetailsActivity.setText(deviceReferenceNumber)
        et_deviceWebsite_deviceDetailsActivity.setText(deviceManufacturerWebsite)
        if (deviceIsBorrowed == false) {
            spin_deviceIsBorrowed_deviceDetailsActivity.setSelection(0)
        } else {
            spin_deviceIsBorrowed_deviceDetailsActivity.setSelection(1)
        }
    }

    fun confirmChangeDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Voulez-vous vraiment modifier ce matériel ?")
        builder.setPositiveButton("Oui") { dialog, which ->
            updateDevice()
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun updateDevice() {
        val id = deviceId
        val type = et_deviceType_deviceDetailsActivity.text.toString()
        val brand = et_deviceBrand_deviceDetailsActivity.text.toString()
        val model = et_deviceModel_deviceDetailsActivity.text.toString()
        val referenceNumber = tv_deviceRefNumber_deviceDetailsActivity.text.toString()
        val manufacturerWebsite = et_deviceWebsite_deviceDetailsActivity.text.toString()
        //val qrCode = et_deviceIsBorrowed_deviceDetailsActivity.text.toString()
        var isBorrowed = deviceIsBorrowed
        if (spin_deviceIsBorrowed_deviceDetailsActivity.selectedItem.toString() == "Disponible") {
            isBorrowed = false
        } else {
            isBorrowed = true
        }

        var deviceToUpdate = DeviceRecord(
            id,
            type,
            brand,
            model,
            referenceNumber,
            manufacturerWebsite,
            deviceQrCode,
            isBorrowed
        )
        GlobalScope.launch(Dispatchers.IO) {
            //val db = Room.databaseBuilder(applicationContext, MyDb::class.java, "MyDataBase").build()
            val dao = db.deviceDao()
            dao.updateDevice(deviceToUpdate)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@DeviceDetailsActivity, "Matériel modifié", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("updatedDevice", deviceToUpdate)
                resultIntent.putExtra("position", position)
                setResult(2, resultIntent)
                finish()
            }
        }
    }

}