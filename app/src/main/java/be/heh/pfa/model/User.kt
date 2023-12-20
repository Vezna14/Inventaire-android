package be.heh.pfa.model

import java.security.MessageDigest

class User (
    var id: Long=0,
    var email: String,
    var rawPassword: String,
    var canWrite: Boolean = false,
    var isActive: Boolean = true
    ) {
    var password: String = hashString(rawPassword) // Le mot de passe est haché lors de l'initialisation

    // Méthode pour vérifier si l'utilisateur a un accès particulier
        fun hasWriteAccess(): Boolean {
            return canWrite
        }
        fun changeWriteAccess(perm: Boolean){
            this.canWrite=perm
        }

    // Méthode pour modifier le mot de passe
    fun changePassword(newPassword: String) {
        this.password = hashString(newPassword)
    }

    //hacher une chaine de caractère (pour le mot de passe)
    fun hashString(input: String): String {
        val bytes = input.toByteArray()
        val digest = MessageDigest.getInstance("SHA-1")
        val hashedBytes = digest.digest(bytes)

        // Convertir les octets hachés en représentation hexadécimale
        val stringBuilder = StringBuilder()
        for (byte in hashedBytes) {
            stringBuilder.append(String.format("%02x", byte))
        }

        return stringBuilder.toString()
    }


}
