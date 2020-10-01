package com.gwtw.spring.repository;

import com.gwtw.spring.domain.User;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DatastoreRepository<User, Long> {

    User getUserByEmail(String email);

    User getUserById(Long id);

}

