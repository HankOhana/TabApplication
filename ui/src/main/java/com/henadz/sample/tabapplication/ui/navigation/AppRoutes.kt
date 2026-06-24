package com.henadz.sample.tabapplication.ui.navigation

import android.os.Parcel
import android.os.Parcelable

data object Setup : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) = Unit

    @JvmField
    val CREATOR: Parcelable.Creator<Setup> =
        object : Parcelable.Creator<Setup> {
            override fun createFromParcel(source: Parcel): Setup = Setup

            override fun newArray(size: Int): Array<Setup?> = arrayOfNulls(size)
        }
}

data class Table(
    val rows: Int,
    val cols: Int,
) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        dest.writeInt(rows)
        dest.writeInt(cols)
    }

    companion object CREATOR : Parcelable.Creator<Table> {
        override fun createFromParcel(source: Parcel): Table = Table(rows = source.readInt(), cols = source.readInt())

        override fun newArray(size: Int): Array<Table?> = arrayOfNulls(size)
    }
}
