package be.heh.pfa.db

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import be.heh.pfa.db.MyDb

class MyDbCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.i("AppDatabaseCallback", "onCreate callback is called")
        // Insérez ici le code pour pré-peupler la base de données
        // Vous pouvez utiliser un objet DAO pour effectuer des opérations d'insertion
        GlobalScope.launch(Dispatchers.IO){
            Log.i("AppDatabaseCallback", "Inserting data into the database")
            val deviceDao = MyDb.getInstance(context).deviceDao()

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

            deviceDao.insertDevice(device1)
            deviceDao.insertDevice(device2)
        }
    }
}
