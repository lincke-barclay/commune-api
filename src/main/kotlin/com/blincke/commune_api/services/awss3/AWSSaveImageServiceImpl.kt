package com.blincke.commune_api.services.awss3

import com.blincke.commune_api.services.images.SaveImageService
import com.blincke.commune_api.services.util.FileService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.net.URL

@Component
class AWSSaveImageServiceImpl(
    private val awsFileUploadService: AWSFileUploadService,
    private val fileService: FileService,
) : SaveImageService {

    @Value("\${linode.region.static}")
    lateinit var region: String

    private fun getUrl(vararg nestedFileName: String): URL {
        val resolvedName = nestedFileName.joinToString(separator = "/")
        return URI(
            "https://" +
                    AWSFileUploadService.BucketLocation.Images.bucketName +
                    ".${region}" +
                    ".linodeobjects.com/" +
                    resolvedName
        ).toURL()
    }

    override fun saveImage(multiFile: MultipartFile, vararg nestedFileName: String): URL {

        val resolvedName = nestedFileName.joinToString(separator = "/")

        val file = fileService.convertMultiPartToFile(multiFile)
        awsFileUploadService.uploadFile(AWSFileUploadService.BucketLocation.Images, resolvedName, file)

        return getUrl(*nestedFileName)
    }
}