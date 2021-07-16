package com.faf.student.elasticsearch.service;

import com.faf.student.elasticsearch.entity.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faf.student.elasticsearch.repository.StudentRepository;
import com.faf.student.utils.Operation;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentService {

	private final StudentRepository studentRepository;

	public StudentService(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	public void maintainReadModel(Map<String, Object> studentData, Operation operation) {
		if (operation == null) {
			throw new IllegalArgumentException("Operation is required");
		}

		final ObjectMapper mapper = new ObjectMapper();
		final Student student = mapper.convertValue(studentData, Student.class);

		if (Operation.DELETE.name().equals(operation.name())) {
			studentRepository.deleteById(student.getId());
		} else {
			studentRepository.save(student);
		}
	}
}
