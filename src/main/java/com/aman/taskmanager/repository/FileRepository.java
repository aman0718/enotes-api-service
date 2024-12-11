package com.aman.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aman.taskmanager.entity.FileDetails;

public interface FileRepository extends JpaRepository<FileDetails, Integer> {

}