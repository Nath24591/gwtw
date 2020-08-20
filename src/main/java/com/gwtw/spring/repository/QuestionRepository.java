package com.gwtw.spring.repository;

import com.gwtw.spring.domain.Question;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends DatastoreRepository<Question, Long> {
    @Query("SELECT * FROM questions limit 1")
    Question getQuestion();
}
