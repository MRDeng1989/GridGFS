package com.example.file.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.file.bean.File;

public interface FileRepository extends MongoRepository<File, String> {

}
