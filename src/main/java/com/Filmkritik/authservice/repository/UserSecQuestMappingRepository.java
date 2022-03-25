package com.Filmkritik.authservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Filmkritik.authservice.entities.SecurityQuestionsEntity;
import com.Filmkritik.authservice.entities.UserSecQuestMappingEntity;

public interface UserSecQuestMappingRepository extends JpaRepository<UserSecQuestMappingEntity, Long> {

	@Query( value = "Select u from UserSecQuestMappingEntity u where u.uid = ?1 ")
	List<UserSecQuestMappingEntity> getByUserId(long userId);

}
