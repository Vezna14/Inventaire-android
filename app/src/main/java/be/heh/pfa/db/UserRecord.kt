package be.heh.pfa.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Classe d'entité Room (utilisée pour représenter la structure de la base de données)
@Entity(tableName = "users")
data class UserRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String,
    val canWrite: Boolean
)