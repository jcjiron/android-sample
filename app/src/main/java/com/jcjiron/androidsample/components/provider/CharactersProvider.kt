package com.jcjiron.androidsample.components.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.jcjiron.androidsample.core.data.local.CharacterDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * CONTENT PROVIDER: comparte los personajes guardados en Room con otras apps,
 * igual que Contactos o la Galería comparten los suyos.
 *
 * Seguridad (ver AndroidManifest.xml):
 * - Solo lectura: insert / update / delete lanzan excepción.
 * - Protegido con el permiso `<applicationId>.permission.READ_CHARACTERS` de nivel `signature`:
 *   solo lo pueden leer apps firmadas con la MISMA llave que esta (las apps de la empresa).
 *   Cualquier otra app, o `adb shell content query`, recibe "Permission Denial".
 */
class CharactersProvider : ContentProvider() {

    /** Hilt no inyecta en ContentProviders (se crean antes que la Application), así que se pide a mano. */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProviderEntryPoint {
        fun characterDao(): CharacterDao
    }

    private val dao: CharacterDao by lazy {
        EntryPointAccessors
            .fromApplication(checkNotNull(context).applicationContext, ProviderEntryPoint::class.java)
            .characterDao()
    }

    private lateinit var uriMatcher: UriMatcher

    override fun onCreate(): Boolean {
        val authority = CharactersContract.authority(checkNotNull(context))
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(authority, CharactersContract.PATH_CHARACTERS, ALL)
            addURI(authority, "${CharactersContract.PATH_CHARACTERS}/#", ONE)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor {
        // Para mantenerlo simple se regresan todas las columnas de CharactersContract.Columns.
        val cursor = when (uriMatcher.match(uri)) {
            ALL -> dao.selectAllCursor()
            ONE -> dao.selectByIdCursor(ContentUris.parseId(uri).toInt())
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
        cursor.setNotificationUri(checkNotNull(context).contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        val authority = CharactersContract.authority(checkNotNull(context))
        return when (uriMatcher.match(uri)) {
            ALL -> "vnd.android.cursor.dir/vnd.$authority.${CharactersContract.PATH_CHARACTERS}"
            ONE -> "vnd.android.cursor.item/vnd.$authority.${CharactersContract.PATH_CHARACTERS}"
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri =
        throw UnsupportedOperationException("CharactersProvider es de solo lectura")

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException("CharactersProvider es de solo lectura")

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException("CharactersProvider es de solo lectura")

    private companion object {
        const val ALL = 1
        const val ONE = 2
    }
}
