package be.heh.pfa.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(UserRecord::class)], version = 1)
abstract class MyDB : RoomDatabase() {
    /* Pour chaque classe DAO associée à la base de données,
    la classe de base de données doit définir une méthode abstraite
    qui ne comporte  aucun argument
    et qui renvoie une instance de la classe DAO.*/
    abstract fun userDao(): UserDao

}