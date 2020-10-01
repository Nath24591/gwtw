package com.gwtw.spring.repository;

import com.gwtw.spring.domain.UserPasswordReset;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPasswordResetRepository extends DatastoreRepository<UserPasswordReset, Long> {
    UserPasswordReset getUserPasswordResetByToken(String token);
}
