package com.privatevault.app.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "folder_id")
    val folderId: String? = null,

    @ColumnInfo(name = "media_type")
    val mediaType: String, // "PHOTO" or "VIDEO"

    @ColumnInfo(name = "encrypted_file_path")
    val encryptedFilePath: String, // relative to filesDir

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,

    @ColumnInfo(name = "encrypted_dek_blob")
    val encryptedDekBlob: ByteArray, // Wrapped by Keystore master key

    @ColumnInfo(name = "dek_key_alias")
    val dekKeyAlias: String,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,

    @ColumnInfo(name = "owner_account_id")
    val ownerAccountId: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaItemEntity
        if (id != other.id) return false
        if (!encryptedDekBlob.contentEquals(other.encryptedDekBlob)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + encryptedDekBlob.contentHashCode()
        return result
    }
}
