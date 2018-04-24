package com.example.file.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.file.bean.File;
import com.example.file.service.IFileService;
import com.example.file.util.MD5Util;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;

@RestController
public class FileController {
	@Autowired
    private IFileService fileService;
    
    @Value("${server.address:dengqw}")
    private String serverAddress;
    
    @Value("${server.port:9090}")
    private String serverPort;
    
    @RequestMapping(value = "/")
    public String index(Model model) {
    	// 展示最新二十条数据
        model.addAttribute("files", fileService.listFilesByPage(0,20)); 
        return "index";
    }

    /**
     * 分页查询文件
     * @param pageIndex
     * @param pageSize
     * @return
     */
	@GetMapping("files/{pageIndex}/{pageSize}")
	public List<File> listFilesByPage(@PathVariable int pageIndex, @PathVariable int pageSize){
		return fileService.listFilesByPage(pageIndex, pageSize);
	}
			
    /**
     * 获取文件片信息
     * @param id
     * @return
     */
    @GetMapping("files/{id}")
    public ResponseEntity<Object> serveFile(@PathVariable String id) {

        File file = fileService.getFileById(id);

        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream" )
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize()+"")
                    .header("Connection",  "close") 
                    .body( file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }
    
    /**
     * 在线显示文件
     * @param id
     * @return
     */
   @GetMapping("/view/{id}")
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) {

        File file = fileService.getFileById(id);

        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType() )
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize()+"")
                    .header("Connection",  "close") 
                    .body( file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }
    }
    
    /**
     * 上传
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
        	File f = new File(file.getOriginalFilename(),  file.getContentType(), file.getSize(), file.getBytes());
        	f.setMd5( MD5Util.getMD5(file.getInputStream()) );
        	fileService.saveFile(f);
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    "Your " + file.getOriginalFilename() + " is wrong!");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";

    }
 
    /**
     * 上传接口
     * @param file
     * @return
     */
    @PostMapping("/uploadfile")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    	File returnFile = null;
        try {
        	File f = new File(file.getOriginalFilename(),  file.getContentType(), file.getSize(),file.getBytes());
        	f.setMd5( MD5Util.getMD5(file.getInputStream()) );
        	returnFile = fileService.saveFile(f);
        	String path = "//"+ serverAddress + ":" + serverPort + "/view/"+returnFile.getId();
        	return ResponseEntity.status(HttpStatus.OK).body(path);
 
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
 
    }
    
	/**
     * 删除文件
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id) {
 
    	try {
			fileService.removeFile(id);
			return ResponseEntity.status(HttpStatus.OK).body("DELETE Success!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }
    
    /**
     * @title 上传文件到GridFS文件系统
     * @param file
     * @return
     */
    @PostMapping("/uploadgfs")
    public ResponseEntity<String> uploadGfs(@RequestParam("file") MultipartFile file){
    	try {
	    	DBObject metadata = new BasicDBObject();
	    	metadata.put("userId", UUID.randomUUID().toString());
	    	metadata.put("userName", "邓清文");
	    	metadata.put("message", "Just So So");
	    	GridFSFile gridFSFile = fileService.saveGridFSFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata);
	    	return ResponseEntity.status(HttpStatus.OK).body(gridFSFile.get("_id").toString());
    	} catch (IOException ex) {
		   ex.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    	}
    }
    
    /**
     * @title 查询GridFS文件系统里的文件
     * @param gridFSFileId
     * @return
     */
    @GetMapping("/getgfs")
    public ResponseEntity<String> queryOneGfs(@RequestParam String gridFSFileId){
    	GridFSFile gridFSFile = fileService.queryOneGridFSFile(gridFSFileId);
    	return ResponseEntity.status(HttpStatus.OK).body(gridFSFile.getFilename());
    }
    
    
    /**
     * @title 删除GridFS文件系统里的文件
     * @param gridFSFileId
     * @return
     */
    @PostMapping("/deletegfs")
    public ResponseEntity<String> deleteOneGfs(@RequestParam String gridFSFileId){
    	fileService.deleteOneGridFSFile(gridFSFileId);
    	return ResponseEntity.status(HttpStatus.OK).body("delelte successfully");
    }

}
