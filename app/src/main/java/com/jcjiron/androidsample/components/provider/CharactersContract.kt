package com.jcjiron.androidsample.components.provider

import android.content.Context
import android.net.Uri

/**
 * Contrato público del ContentProvider: la dirección (URI) y las columnas.
 * Es lo que le compartirías a otra app de la empresa para que lea los personajes.
 *
 *   content://<applicationId>.provider/characters        → todos
 *   content://<applicationId>.provider/characters/<id>   → uno
 */
object CharactersContract {

    const val PATH_CHARACTERS = "characters"

    fun authority(context: Context): String = "${context.packageName}.provider"

    fun contentUri(context: Context): Uri =
        Uri.parse("content://${authority(context)}/$PATH_CHARACTERS")

    /** Permiso que necesita otra app para leer (solo apps firmadas con la misma llave). */
    fun readPermission(context: Context): String = "${context.packageName}.permission.READ_CHARACTERS"

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val STATUS = "status"
        const val SPECIES = "species"
        const val GENDER = "gender"
        const val ORIGIN = "origin"
        const val LOCATION = "location"
        const val IMAGE_URL = "imageUrl"
    }
}
