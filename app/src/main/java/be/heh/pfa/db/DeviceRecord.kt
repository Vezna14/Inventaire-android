package be.heh.pfa.db

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var type: String?,
    var brand: String?,
    var model: String?,
    var referenceNumber: String?,
    var manufacturerWebsite: String?,
    var qrCode: String?,
    var isBorrowed: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun describeContents(): Int {

        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(type)
        dest.writeString(brand)
        dest.writeString(model)
        dest.writeString(referenceNumber)
        dest.writeString(manufacturerWebsite)
        dest.writeString(qrCode)
        dest.writeByte(if (isBorrowed) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<DeviceRecord> {
        override fun createFromParcel(parcel: Parcel): DeviceRecord {
            return DeviceRecord(parcel)
        }

        override fun newArray(size: Int): Array<DeviceRecord?> {
            return arrayOfNulls(size)
        }
    }
}