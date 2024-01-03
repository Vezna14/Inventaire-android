package be.heh.pfa

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.db.DeviceRecord
import be.heh.pfa.db.MyDb
import be.heh.pfa.inventory.DeviceDetailsActivity
import be.heh.pfa.model.AuthenticatedUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

public class DeviceAdapter(private val devices: MutableList<DeviceRecord>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private var editDeviceLauncher: ActivityResultLauncher<Intent>? = null

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceIcon: ImageView = itemView.findViewById(R.id.iv_deviceIcon_deviceCardViewItem)
        val deviceTypeTextView: TextView =
            itemView.findViewById(R.id.tv_deviceType_deviceCardViewItem)
        val deviceBrandTextView: TextView =
            itemView.findViewById(R.id.tv_deviceBrand_deviceCardViewItem)
        val deviceModelTextView: TextView =
            itemView.findViewById(R.id.tv_deviceModel_deviceCardViewItem)
        val deviceReferenceNumberTextView: TextView =
            itemView.findViewById(R.id.tv_deviceRefNumber_deviceCardViewItem)
        val deviceManufacturerWebsiteTextView: TextView =
            itemView.findViewById(R.id.tv_deviceManufacturerWebsite_deviceCardViewItem)

        //val deviceQrCodeTextView: ImageView = itemView.findViewById(R.id.iv_deviceQrCode_deviceCardViewItem)
        val deviceIsBorrowedImageView: ImageView =
            itemView.findViewById(R.id.iv_deviceStatus_deviceCardViewItem)
        val deviceEditImageView: ImageView =
            itemView.findViewById(R.id.iv_devicEdit_deviceCardViewItem)
        val deviceDeleteImageView: ImageView =
            itemView.findViewById(R.id.iv_deleteDevice_deviceCardViewItem)
    }

    //onCreateViewHolder crée la vue qui va afficher les items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val deviceView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.device_cardview_item, parent, false)

        return DeviceViewHolder(deviceView)
    }

    //onBindViewHolder : binder la liste des items avec les widgets (imageView, TextView etc)
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val currentDevice = devices[position]
        //cacher le bouton d'édition si l'utilisateur n'a pas les droits
        if (AuthenticatedUser.canWrite) {
            holder.deviceEditImageView.visibility = View.VISIBLE
            holder.deviceDeleteImageView.visibility = View.VISIBLE
        } else {
            holder.deviceEditImageView.visibility = View.GONE
            holder.deviceDeleteImageView.visibility = View.GONE
        }
        //affecter les valeurs aux widgets
        holder.deviceTypeTextView.text = currentDevice.type
        holder.deviceBrandTextView.text = currentDevice.brand
        holder.deviceModelTextView.text = currentDevice.model
        holder.deviceReferenceNumberTextView.text = currentDevice.referenceNumber
        holder.deviceManufacturerWebsiteTextView.text = currentDevice.manufacturerWebsite
        // icone en fct du type de device
        if (currentDevice.type == "Smartphone") {
            holder.deviceIcon.setImageResource(R.drawable.ic_smartphone)
        } else {
            holder.deviceIcon.setImageResource(R.drawable.ic_tablet)
        }
        // holder.deviceIcon.setImageResource(R.drawable.ic_device) // Remplacez par votre ressource d'icône
        // holder.deviceQrCodeTextView.setImageBitmap(generateQRCode(currentDevice.qrCode)) // Remplacez par votre méthode de génération de QR Code

        // Exemple d'utilisation pour définir l'image isBorrowed en fonction de l'état
        if (currentDevice.isBorrowed) {
            holder.deviceIsBorrowedImageView.setImageResource(R.drawable.ic_device_borrowed)
        } else {
            holder.deviceIsBorrowedImageView.setImageResource(R.drawable.ic_device_available)
        }

        // définir des écouteurs de clic
        //holder.itemView.setOnClickListener { // Logique à exécuter lorsqu'un élément est cliqué }
        holder.deviceManufacturerWebsiteTextView.setOnClickListener {
            // Logique à exécuter lorsqu'un élément est cliqué
            //Toast.makeText(holder.itemView.context, "Lien vers le site du fabricant", Toast.LENGTH_SHORT).show()
            val intent = Intent(holder.itemView.context, WebViewActivity::class.java)
            intent.putExtra("url", currentDevice.manufacturerWebsite)
            holder.itemView.context.startActivity(intent)
        }
        holder.deviceEditImageView.setOnClickListener {
            /*
            //afficher les détails de l'item dans une autre vue et pouvoir y faire des modifications ( il a fallu passer UserRecord en parcelable pour pouvoir passer l'objet en extra)
            var intent = Intent(holder.itemView.context, DeviceDetailsActivity::class.java)
            intent.putExtra("device", currentDevice)
            intent.putExtra("position", position)
            //(holder.itemView.context as AppCompatActivity).startActivityForResult(intent, 2)
            (myContext as AppCompatActivity).startActivityForResult(intent, 2)
            //holder.itemView.context.startActivity(intent)
*/
            val inflater = LayoutInflater.from(holder.itemView.context)
            val dialogView = inflater.inflate(R.layout.device_details_alert_dialog_layout, null)

            val spinnerType = dialogView.findViewById<Spinner>(R.id.spin_deviceType_deviceDetailsDialog)

            val editTextBrand = dialogView.findViewById<EditText>(R.id.et_deviceBrand_deviceDetailsDialog)
            val editTextModel = dialogView.findViewById<EditText>(R.id.et_deviceModel_deviceDetailsDialog)
            val editTextRefNumber = dialogView.findViewById<EditText>(R.id.et_deviceRefnumber_deviceDetailsDialog)
            val editTextManufacturerWebsite = dialogView.findViewById<EditText>(R.id.et_manufacturerWebsite_deviceDetailsDialog)
            val checkBoxIsBorrowed = dialogView.findViewById<CheckBox>(R.id.checkBoxIsBorrowed)

            // Remplissez les champs avec les informations de currentDevice
            if (currentDevice.type == "Smartphone") {
                spinnerType.setSelection(0)
            } else {
                spinnerType.setSelection(1)
            }
            editTextBrand.setText(currentDevice.brand)
            editTextModel.setText(currentDevice.model)
            editTextRefNumber.setText(currentDevice.referenceNumber)
            editTextManufacturerWebsite.setText(currentDevice.manufacturerWebsite)
            checkBoxIsBorrowed.isChecked = !currentDevice.isBorrowed

            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle("Modifier l'appareil")
                .setView(dialogView)
                .setPositiveButton("Enregistrer") { dialog, which ->
                    // Logique pour sauvegarder les modifications
                    // Mettez à jour les propriétés de currentDevice avec les valeurs des EditText
                    currentDevice.type = spinnerType.selectedItem.toString()
                    currentDevice.brand = editTextBrand.text.toString()
                    currentDevice.model = editTextModel.text.toString()
                    currentDevice.referenceNumber = editTextRefNumber.text.toString()
                    currentDevice.manufacturerWebsite = editTextManufacturerWebsite.text.toString()
                    currentDevice.isBorrowed = !checkBoxIsBorrowed.isChecked

                    GlobalScope.launch(Dispatchers.IO) {
                        val db = MyDb.getInstance(holder.itemView.context)
                        db.deviceDao().updateDevice(currentDevice)
                        withContext(Dispatchers.Main) { Toast.makeText(holder.itemView.context, "Matériel modifié", Toast.LENGTH_SHORT).show() }
                    }
                    // Mettre à jour l'affichage
                    updateRecyclerViewWithUpdatedDevice(currentDevice, position)
                }
                .setNegativeButton("Annuler") { dialog, which ->
                    // Logique pour annuler les modifications
                    dialog.dismiss()
                }
                .show()
        }

        holder.deviceDeleteImageView.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer ce matériel ?")
                .setPositiveButton("Oui") { dialog, which ->

                    GlobalScope.launch(Dispatchers.IO) {
                        val db = MyDb.getInstance(holder.itemView.context)
                        db.deviceDao().deleteDevice(currentDevice)
                    }
                    // Mettre à jour l'affichage
                    devices.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, devices.size)

                }
                .setNegativeButton("Non") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    //retourne le nombre d'item présent dans la liste
    override fun getItemCount(): Int {
        return devices.size
    }

    fun updateRecyclerViewWithUpdatedDevice(updatedDevice: DeviceRecord, position: Int) {
        devices[position] = updatedDevice
        notifyItemChanged(position)
        notifyDataSetChanged()

    }

    fun addDeviceInRecyclerView(newDevice: DeviceRecord) {
        devices.add(newDevice)
        notifyItemInserted(devices.size - 1)
        notifyDataSetChanged()
    }
}








