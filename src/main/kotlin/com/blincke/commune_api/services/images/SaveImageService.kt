package com.blincke.commune_api.services.images

import org.springframework.web.multipart.MultipartFile
import java.net.URL

interface SaveImageService {
    fun saveImage(multiFile: MultipartFile, vararg nestedFileName: String): URL
}