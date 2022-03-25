package com.Filmkritik.authservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Filmkritik.authservice.entities.SecurityQuestionsEntity;
import com.Filmkritik.authservice.entities.UserTVMap;

public interface UserTVMapRepository extends JpaRepository<UserTVMap, Long> {

	@Query( value = "Select u.tid from UserTVMap u where u.uid = ?1 ")
	List<Long> getLikedTvByUser(long userId);
}
