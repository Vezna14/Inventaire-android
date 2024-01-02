package be.heh.pfa.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserRecord::class,DeviceRecord::class], version = 1)
abstract class MyDb : RoomDatabase() {
    /* Pour chaque classe DAO associée à la base de données,
    la classe de base de données doit définir une méthode abstraite
    qui ne comporte  aucun argument
    et qui renvoie une instance de la classe DAO.*/
    abstract fun userDao(): UserDao
    abstract fun deviceDao(): DeviceDao


    companion object {
        @Volatile
        private var INSTANCE: MyDb? = null

        fun getInstance(context: Context): MyDb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDb::class.java,
                    "MyDataBase"
                )
                    .addCallback(MyDbCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}