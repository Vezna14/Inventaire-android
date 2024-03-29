package be.heh.pfa

import be.heh.pfa.db.UserRecord

object AuthenticatedUser {
    var id: Long = 0
    var email: String = "N/A"
    var canWrite: Boolean = false
    var isActive: Boolean = true
    var isSuperAdmin: Boolean = false
    var password: String = ""


    fun setAuthenticatedUserInfo(userRecord: UserRecord) {
        id = userRecord.id
        email = userRecord.email
        canWrite = userRecord.canWrite
        isActive = userRecord.isActive
        isSuperAdmin = userRecord.isSuperAdmin
        password = userRecord.password
    }

    fun clearAuthenticatedUser() {
        id = 0
        email = ""
        canWrite = false
        isActive = true
        isSuperAdmin = false
        password = ""
    }
}