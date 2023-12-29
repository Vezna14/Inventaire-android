package be.heh.pfa.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// Classe d'entité Room (utilisée pour représenter la structure de la base de données)
@Entity(tableName = "users")
data class UserRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var email: String,
    var password: String,
    var canWrite: Boolean = false,
    var isActive: Boolean = true,
    var isSuperAdmin: Boolean = false
)