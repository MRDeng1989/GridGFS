package com.example.file.service;

import java.io.InputStream;
import java.util.List;

import com.example.file.bean.File;
import com.mongodb.gridfs.GridFSFile;


public interface IFileService {
	
	/**
	 * 保存文件
	 * @param File
	 * @return
	 */
	File saveFile(File file);
	
	/**
	 * 删除文件
	 * @param File
	 * @return
	 */
	 void removeFile(String id);
	
	/**
	 * 根据id获取文件
	 * @param File
	 * @return
	 */
	 File getFileById(String id);

	/**
	 * 分页查询，按上传时间降序
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<File> listFilesByPage(int pageIndex, int pageSize);
	
	
	/**
	 * @title 文件存储到GridFS系统
	 * @param content
	 * @param filename
	 * @param contentType
	 * @param metadata
	 * @return
	 */
	GridFSFile saveGridFSFile(InputStream content, String filename, String contentType, Object metadata);
	
	/**
	 * @title 查询文件
	 * @param gridFSFileId
	 * @return
	 */
	GridFSFile queryOneGridFSFile(String gridFSFileId);
	
	/**
	 * @title 删除文件
	 * @param gridFSFileId
	 */
	void deleteOneGridFSFile(String gridFSFileId);
	

}
