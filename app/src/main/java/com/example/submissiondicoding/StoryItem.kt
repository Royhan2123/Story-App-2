package com.example.submissiondicoding

import android.os.Parcel
import android.os.Parcelable

class StoryItemParcelable(
    val id: String?,
    val name: String?,
    private val description: String?,
    private val photoUrl: String?
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoryItemParcelable> {
        override fun createFromParcel(parcel: Parcel): StoryItemParcelable {
            val id = parcel.readString()
            val name = parcel.readString()
            val description = parcel.readString()
            val photoUrl = parcel.readString()

            return StoryItemParcelable(id, name, description, photoUrl)
        }

        override fun newArray(size: Int): Array<StoryItemParcelable?> {
            return arrayOfNulls(size)
        }
    }
}