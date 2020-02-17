package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.FileDto
import com.cj.ticketsys.svc.FileStorage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/ota/v1/manage/file")
class ManageFileController : BaseController() {

    @Autowired
    private lateinit var fileStorage: FileStorage


    @PostMapping(value = ["upload"], consumes = arrayOf(MediaType.MULTIPART_FORM_DATA_VALUE))
    fun upload(
        @RequestPart("file") file: MultipartFile?
    ): ResultT<FileDto> {
        if (file == null) {
            return ResultT(RESULT_FAIL, "图片未上传")
        }
        if (file.getSize() >= FileStorage.MAX_FILE_SIZE) {
            return ResultT(RESULT_FAIL, "图片过大")
        }
        try {
            val fileName = file.originalFilename
            val suffix = fileName!!.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
            val validFormat = "" != suffix && ("jpg".equals(suffix) || "jpeg".equals(suffix)
                    || "png".equals(suffix) || "gif".equals(suffix))
            if (!validFormat) {
                return ResultT(RESULT_FAIL, "图片格式错误")
            }
            val fResult = fileStorage.uploadFile(fileName, file.getInputStream())
            if (fResult != null) {
                val dto = FileDto()
                dto.fileUrl = fResult.fileUrl
                dto.storeId = fResult.storeId
                return ResultT(RESULT_SUCCESS, "成功", dto)
            }
        } catch (e: Exception) {
            //异常情况
            e.printStackTrace()
        }
        return ResultT(RESULT_FAIL, "图片上传失败")
    }
}