package com.cj.ticketsys.svc

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.lokra.seaweedfs.core.FileTemplate
import java.util.HashMap
import java.io.IOException
import java.io.InputStream
import org.lokra.seaweedfs.core.FileSource


@Component
class FileStorage {

    @Value("\${weedfs.host}")
    private var host: String = ""
    @Value("\${weedfs.port}")
    private var port: Int = 0

    private val MAX_NAME_LENGTH: Int = 32

    companion object {
        var fileSource: FileSource? = null
        val MAX_FILE_SIZE = 1048576 shl 2
    }

    @Throws(IOException::class)
    private fun initialConfig() {
        fileSource!!.setHost(host)
        fileSource!!.setPort(port)
        fileSource!!.startup()
    }

    @Throws(IOException::class)
    fun initialFileSource() {
        if (fileSource == null) {
            fileSource = FileSource()
            initialConfig()
        }
    }

    /**
     * 上传文件流到服务器
     *
     * @param fileName
     * @param in
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uploadFile(fileName: String, inputStream: InputStream): FileUploadResult? {
        var fn = fileName
        initialFileSource()
        try {
            if (fn.length > MAX_NAME_LENGTH) {
                val suffix = fn.substring(fn.lastIndexOf(",") + 1)
                fn = fileName.substring(0, MAX_NAME_LENGTH) + suffix
            }
            fn = fn.replace(" ".toRegex(), "")
            val template = FileTemplate(fileSource!!.connection)
            val fileHandleStatus = template.saveFileByStream(fn, inputStream)
            var fileUrl = template.getFileUrl(fileHandleStatus.fileId)
            fileUrl = fileUrl.replace(",", "/") + "/" + fn
            return FileUploadResult(fileHandleStatus.fileId, fileUrl)
        } catch (ee: Exception) {
            ee.printStackTrace()
            return null
        }
    }
}

data class FileUploadResult(val storeId: String, val fileUrl: String)