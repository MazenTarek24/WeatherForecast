package com.example.weatherforecast.localsource

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class FavoriteLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lat: Double,
    val lon: Double,
    val name: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoriteLocationEntity> {
        override fun createFromParcel(parcel: Parcel): FavoriteLocationEntity {
            return FavoriteLocationEntity(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteLocationEntity?> {
            return arrayOfNulls(size)
        }
    }
}