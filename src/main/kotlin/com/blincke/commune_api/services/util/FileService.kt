package com.blincke.commune_api.services.util

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

@Service
class FileService {
    fun convertMultiPartToFile(multiFile: MultipartFile): File {
        multiFile.originalFilename?.let { fileName ->
            return File(fileName).also { file ->
                val fos = FileOutputStream(file)
                fos.write(multiFile.bytes)
                fos.close()
            }
        } ?: throw Exception("Expected non null filename")
    }
}