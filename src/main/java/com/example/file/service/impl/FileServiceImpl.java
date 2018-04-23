package com.example.file.service.impl;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.example.file.bean.File;
import com.example.file.repository.FileRepository;
import com.example.file.service.IFileService;
import com.mongodb.gridfs.GridFSFile;


@Service
public class FileServiceImpl implements IFileService {

	@Autowired
	public FileRepository fileRepository;
	
	@Autowired
	public GridFsTemplate gridFsTemplate;

	@Override
	public File saveFile(File file) {
		return fileRepository.save(file);
	}

	@Override
	public void removeFile(String id) {
		fileRepository.delete(id);
	}

	@Override
	public File getFileById(String id) {
		return fileRepository.findOne(id);
	}

	@Override
	public List<File> listFilesByPage(int pageIndex, int pageSize) {
		Page<File> page = null;
		List<File> list = null;
		Sort sort = new Sort(Direction.DESC,"uploadDate"); 
		Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
		page = fileRepository.findAll(pageable);
		list = page.getContent();
		return list;
	}

	@Override
	public GridFSFile saveGridFSFile(InputStream content, String filename, String contentType, Object metadata) {
		return gridFsTemplate.store(content, filename, contentType, metadata);
	}

	@Override
	public GridFSFile queryOneGridFSFile(String gridFSFileId) {
		Criteria criteria = GridFsCriteria.where("_id").is(gridFSFileId);
		return gridFsTemplate.findOne(new Query().addCriteria(criteria));
	}

	@Override
	public void deleteOneGridFSFile(String gridFSFileId) {
		Criteria criteria = GridFsCriteria.where("_id").is(gridFSFileId);
		gridFsTemplate.delete(new Query().addCriteria(criteria));
	}
}
