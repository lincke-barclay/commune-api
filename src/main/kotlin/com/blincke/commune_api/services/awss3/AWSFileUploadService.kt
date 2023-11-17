package com.blincke.commune_api.services.awss3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.stereotype.Service
import java.io.File

@Service
class AWSFileUploadService(
    private val amazonS3: AmazonS3,
) {
    enum class BucketLocation(val bucketName: String) {
        Images("communeo-images"),
    }

    fun uploadFile(location: BucketLocation, fileName: String, file: File) {
        amazonS3.putObject(
            PutObjectRequest(location.bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        )
    }
}