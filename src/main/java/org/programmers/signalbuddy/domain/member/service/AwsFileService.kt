package org.programmers.signalbuddy.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@Service
@Transactional
class AwsFileService(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") val bucket: String,
    @Value("\${cloud.aws.s3.folder}") val profileImageDir: String,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * S3에서 프로필 이미지를 가져옵니다.
     *
     * @param filename S3에 저장된 파일명
     * @return Resource 객체
     * @throws IllegalStateException 유효하지 않은 URL 또는 읽을 수 없는 파일인 경우
     */
    fun getProfileImage(filename: String): Resource {
        val filePath = profileImageDir + filename
        val url: URL = generateFileUrl(filePath)

        try {
            val resource: UrlResource = createUrlResource(url)

            if (!resource.exists() || !resource.isReadable) {
                logger.error { "유효하지 않은 파일: $filePath" }
                throw BusinessException(MemberErrorCode.PROFILE_IMAGE_LOAD_ERROR_NOT_EXIST_FILE)
            }

            return resource
        } catch (e: MalformedURLException) {
            logger.error { "URL 생성 실패: $filePath $e" }
            throw BusinessException(MemberErrorCode.PROFILE_IMAGE_LOAD_ERROR_INVALID_URL)
        }
    }

    /**
     * S3에 프로필 이미지를 저장합니다.
     *
     * @param profileImage 업로드할 프로필 이미지 파일
     * @return 저장된 파일 이름
     * @throws IllegalStateException 파일 업로드 중 오류가 발생한 경우
     */
    fun saveProfileImage(profileImage: MultipartFile): String {
        val uniqueFilename = UUID.randomUUID().toString() + "-" + profileImage.originalFilename
        val key = profileImageDir + uniqueFilename

        try {
            profileImage.inputStream.use { inputStream ->
                uploadToS3(key, inputStream, profileImage.contentType, profileImage.size)
                return uniqueFilename
            }
        } catch (e: IOException) {
            logger.error { "파일 업로드 실패: ${profileImage.originalFilename} $e" }
            throw BusinessException(MemberErrorCode.PROFILE_IMAGE_UPLOAD_FAILURE)
        }
    }

    /**
     * S3 URL 생성
     *
     * @param key S3 객체 키
     * @return URL 객체
     */
    private fun generateFileUrl(key: String): URL {
        val request = GetUrlRequest.builder().bucket(bucket).key(key).build()
        return s3Client.utilities().getUrl(request)
    }

    /**
     * S3에 파일 업로드
     *
     * @param key         S3 객체 키
     * @param inputStream 업로드할 파일의 InputStream
     * @param contentType 파일의 Content-Type
     * @param size        파일 크기
     * @throws IllegalStateException S3에 파일 업로드 중 오류가 발생한 경우
     */
    private fun uploadToS3(key: String, inputStream: InputStream, contentType: String, size: Long) {
        try {
            val putObjectRequest = PutObjectRequest.builder().bucket(bucket)
                .key(key).contentType(contentType).build()

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size))
        } catch (e: Exception) {
            logger.error { "파일 업로드 실패: $key $e" }
            throw BusinessException(MemberErrorCode.S3_UPLOAD_FAILURE)
        }
    }

    /**
     * UrlResource 생성 (테스트를 위한 메서드 분리)
     *
     * @param url 생성할 URL
     * @return UrlResource 객체
     * @throws MalformedURLException URL이 잘못된 경우
     */
    @Throws(MalformedURLException::class)
    protected fun createUrlResource(url: URL): UrlResource {
        return UrlResource(url)
    }
}