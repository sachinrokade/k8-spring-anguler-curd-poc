package com.example.k8poc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.k8poc.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

}
