package com.br.strudylab.aws_course_project01.repository;

import com.br.strudylab.aws_course_project01.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByCode(String code);
}
