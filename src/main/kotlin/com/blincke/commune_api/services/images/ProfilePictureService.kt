package com.blincke.commune_api.services.images

import com.blincke.commune_api.models.database.users.User
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.*

@Service
class ProfilePictureService(
    private val saveImageService: SaveImageService,
) {
    fun saveProfilePhotoFor(multiFile: MultipartFile, user: User): URL {
        val obscuredUID = UUID.nameUUIDFromBytes(user.firebaseId.toByteArray()).toString()
        return saveImageService.saveImage(multiFile, "images", "profile", obscuredUID)
    }
}
