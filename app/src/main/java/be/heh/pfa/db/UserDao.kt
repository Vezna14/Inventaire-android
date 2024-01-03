package be.heh.pfa.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): MutableList<UserRecord>?

    @Query("SELECT * FROM Users WHERE email = :email")
    fun getUserByEmail(email: String): UserRecord?

    @Query("SELECT * FROM Users WHERE id = :id")
    fun getUserById(id: String): UserRecord?

    @Query("SELECT * FROM Users WHERE email = :email AND password=:password")
    fun getUserByEmailAndPassword(email: String, password: String): UserRecord?

    @Query("SELECT EXISTS (SELECT * FROM users WHERE isSuperAdmin = 1)")
    fun hasAtLeastOneUser(): Boolean


    @Insert
    fun insertUser(vararg user: UserRecord)

    @Update
    fun updateUser(user: UserRecord)

    @Delete
    fun deleteUser(user: UserRecord)

}