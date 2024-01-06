package com.blincke.commune_api.configuration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AWS3Configuration {
    @Value("\${linode.credentials.access-key}")
    lateinit var accessKey: String

    @Value("\${linode.credentials.secret-key}")
    lateinit var accessSecret: String

    @Value("\${linode.region.static}")
    lateinit var region: String

    val linodeUrl get() = "https://$region.linodeobjects.com"

    @Bean
    fun linodeClient(): AmazonS3 {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, accessSecret)
        return AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    linodeUrl, region
                )
            )
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
    }
}