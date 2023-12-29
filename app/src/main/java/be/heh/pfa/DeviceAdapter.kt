package be.heh.pfa

import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.pfa.db.DeviceRecord
import be.heh.pfa.db.MyDb
import be.heh.pfa.db.UserRecord
import be.heh.pfa.inventory.DeviceDetailsActivity
import be.heh.pfa.model.AuthenticatedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

public class DeviceAdapter (private val devices: MutableList<DeviceRecord>) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceIcon: ImageView = itemView.findViewById(R.id.iv_deviceIcon_deviceCardViewItem)
        val deviceTypeTextView: TextView = itemView.findViewById(R.id.tv_deviceType_deviceCardViewItem)
        val deviceBrandTextView: TextView = itemView.findViewById(R.id.tv_deviceBrand_deviceCardViewItem)
        val deviceModelTextView: TextView = itemView.findViewById(R.id.tv_deviceModel_deviceCardViewItem)
        val deviceReferenceNumberTextView: TextView = itemView.findViewById(R.id.tv_deviceRefNumber_deviceCardViewItem)
        val deviceManufacturerWebsiteTextView: TextView = itemView.findViewById(R.id.tv_deviceManufacturerWebsite_deviceCardViewItem)
        val deviceQrCodeTextView: ImageView = itemView.findViewById(R.id.iv_deviceQrCode_deviceCardViewItem)
        val deviceIsBorrowedImageView: ImageView = itemView.findViewById(R.id.iv_deviceStatus_deviceCardViewItem)
        val deviceEditImageView: ImageView = itemView.findViewById(R.id.iv_devicEdit_deviceCardViewItem)


    }


    //onCreateViewHolder crée la vue qui va afficher les items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val deviceView =
            LayoutInflater.from(parent.context).inflate(R.layout.device_cardview_item, parent, false)


        return DeviceViewHolder(deviceView)
    }

    //onBindViewHolder : binder la liste des items avec les widgets (imageView, TextView etc)
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val currentDevice = devices[position]
        //cacher le bouton d'édition si l'utilisateur n'a pas les droits
        if(AuthenticatedUser.canWrite){
            holder.deviceEditImageView.visibility = View.VISIBLE
        }
        else{
            holder.deviceEditImageView.visibility = View.GONE
        }
        //affecter les valeurs aux widgets
        holder.deviceTypeTextView.text = currentDevice.type
        holder.deviceBrandTextView.text = currentDevice.brand
        holder.deviceModelTextView.text = currentDevice.model
        holder.deviceReferenceNumberTextView.text = currentDevice.referenceNumber
        holder.deviceManufacturerWebsiteTextView.text = currentDevice.manufacturerWebsite
        // icone en fct du type de device
        if (currentDevice.type == "Mobile" ){
            holder.deviceIcon.setImageResource(R.drawable.ic_smartphone)
        }
        else{holder.deviceIcon.setImageResource(R.drawable.ic_tablet)}
        // holder.deviceIcon.setImageResource(R.drawable.ic_device) // Remplacez par votre ressource d'icône
        // holder.deviceQrCodeTextView.setImageBitmap(generateQRCode(currentDevice.qrCode)) // Remplacez par votre méthode de génération de QR Code

        // Exemple d'utilisation pour définir l'image isBorrowed en fonction de l'état
        if (currentDevice.isBorrowed) {
            holder.deviceIsBorrowedImageView.setImageResource(R.drawable.ic_device_borrowed)
        } else {
            holder.deviceIsBorrowedImageView.setImageResource(R.drawable.ic_device_available)
        }

        // définir des écouteurs de clic pour les éléments si nécessaire
        //holder.itemView.setOnClickListener { // Logique à exécuter lorsqu'un élément est cliqué }
        holder.deviceEditImageView.setOnClickListener {

            //afficher les détails de l'item dans une autre vue et pouvoir y faire des modifications ( il a fallu passer UserRecord en parcelable pour pouvoir passer l'objet en extra
            var intent = Intent(holder.itemView.context, DeviceDetailsActivity::class.java)
            intent.putExtra("device", currentDevice)
            holder.itemView.context.startActivity(intent)

        }

    }


    /*  AlertDialog.Builder(holder.itemView.context)
                      .setTitle("Options")
                      .setItems(R.array.permissions_array) { dialog, which ->
                          when (which) {
                              0 -> {
                                  // Code à exécuter lorsque l'option "Modifier" est sélectionnée
                                  Toast.makeText(
                                      holder.itemView.context,
                                      "Option Modifier sélectionnée",
                                      Toast.LENGTH_SHORT
                                  ).show()
                              }
                              1 -> {
                                  // Code à exécuter lorsque l'option "Supprimer" est sélectionnée
                                  Toast.makeText(
                                      holder.itemView.context,
                                      "Option Supprimer sélectionnée",
                                      Toast.LENGTH_SHORT
                                  ).show()
                              }
                          }
                      }
                      .show()
                  */


    //retourne le nombre d'item présent dans la liste
    override fun getItemCount(): Int {
        return devices.size
    }

}



