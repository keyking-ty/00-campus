package com.telit.info.web.controller.app;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.telit.info.data.FileData;
import com.telit.info.data.FileUploadResult;
import com.telit.info.util.CommonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(tags = { "文件上传接口" })
@RequestMapping("/file")
@Slf4j
@RestController
public class FileController {
	
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@Value("${imageType}")
	private String imageType;
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean saveFile(String module,MultipartFile file,FileUploadResult result){
		if (file.isEmpty()) {
			result.setSucc(false);
			result.setMsg("文件为空");
			return false;
		}
		String ext = CommonUtil.getSuffix(file.getOriginalFilename());
		if (!CommonUtil.checkFileExt(ext,imageType)) {
			result.setSucc(false);
			result.setMsg("不支持此文件格式");
			return false;
		}
		String filePath = getPath(ext,module);
		File dest = new File(uploadFilePath + "/" + filePath);
		if (!dest.getParentFile().exists()) {//判断文件父目录是否存在
			dest.getParentFile().mkdir();
		}
		try {
			file.transferTo(dest);//保存文件
			result.setSucc(true);
			result.fill(filePath);
			return true;
		} catch (Exception e) {
			String error = "上传文件" + file.getOriginalFilename() + "失败！";
			result.setSucc(false);
			result.setMsg(error);
			log.error(error,e);
			return false;
		}
	}
	
	@ApiOperation("文件上传,支持格式：jpg,png,jpeg,JPG,PNG,JPEG")
	@PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
	public FileUploadResult fileUpload(
			@ApiParam(name = "file", value = "要上传的文件",required = true,type = "file")
			MultipartFile file,
			@ApiParam(name = "module", value = "服务器存放目录，IOS和Android最好分开",required = false)
			@RequestParam String module){
		FileUploadResult result = new FileUploadResult();
		saveFile(module,file,result);
		return result;
	}
	
	
	@ApiOperation("文件下载")
	@GetMapping("/downLoad")
	public void downLoad(HttpServletResponse response,
			@ApiParam(name = "fileName", value = "文件有效url",required = true)
			@RequestParam String fileName) {
		try {
			FileData data = CommonUtil.getDataFromFile(uploadFilePath,fileName);
			if (data != null) {
				response.setContentType("application/octet-stream; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(data.getName(), "UTF-8"));
				response.getOutputStream().write(data.getData());
			}
		} catch (Exception e) {
			log.error("下载文件" + fileName + "失败",e);
		}
	}
	
	private String getPath(String ext, String module) {
		String filePath = "";
		if (CommonUtil.isNotEmpty(module)) {
			File dest = new File(uploadFilePath + "/" + module);
			if (!dest.exists()) {
				dest.mkdir();
			}
			filePath += module;
		}
		filePath += "/" + dateFormat.format(new Date()) + "/" + System.currentTimeMillis() + "." + ext;
		return filePath;
	}
	
	
	@ApiOperation("百度编辑器上传文件")
	@PostMapping(value = "/ueditorupload", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> ueditorupload(HttpServletRequest request,
			@ApiParam(name = "module", value = "服务器存放目录",required = true)
			@Param(value = "module") String module) {
		Map<String, Object> result = new HashMap<String, Object>();
		MultipartHttpServletRequest mReq = null;
		MultipartFile multipartFile = null;
		String fileName = "";
		//原始文件名   UEDITOR创建页面元素时的alt和title属性
		String originalFileName = "";
		try {
			mReq = (MultipartHttpServletRequest) request;
			//从config.json中取得上传文件的ID
			multipartFile = mReq.getFile("upfile");
			//取得文件的原始文件名称
			fileName = multipartFile.getOriginalFilename();
			//获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
			String ext = CommonUtil.getSuffix(multipartFile.getOriginalFilename());
			if (!CommonUtil.checkFileExt(ext,imageType)) {
				result.put("state", "文件格式错误，上传失败!");
				result.put("url", "");
				result.put("title", "");
				result.put("original", "");
				return result;
			}
			//获取文件路径
			String filePath = getPath(ext,module);
			File file = new File(uploadFilePath + "/" + filePath);
			//如果目录不存在，则创建
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			multipartFile.transferTo(file);//保存文件
			result.put("state","SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
			result.put("url",filePath);
			result.put("title",originalFileName);
			result.put("original",originalFileName);
		} catch (Exception e) {
			String error = "文件 " + fileName + " 上传失败!";
			result.put("state", error);
			result.put("url","");
			result.put("title","");
			result.put("original","");
			log.error(error,e);
		}
		return result;
	}
}
