package com.codework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codework.entity.FileDetails;

public interface FileRepository extends JpaRepository<FileDetails, Integer> {

}
