package com.Filmkritik.authservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.Filmkritik.authservice.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	UserEntity findByUsername(String username);
//	@Query( value = "Update u from UserEntity u Set u.password=?2 where u.uid = ?1  ")
//	void updatePassword(long userId, String password);
}
