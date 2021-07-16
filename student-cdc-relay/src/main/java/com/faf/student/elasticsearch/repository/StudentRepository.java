package com.faf.student.elasticsearch.repository;

import com.faf.student.elasticsearch.entity.Student;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends ElasticsearchRepository<Student, Integer> {
}
