package com.Filmkritik.authservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Filmkritik.authservice.entities.SecurityQuestionsEntity;
import com.Filmkritik.authservice.entities.UserMovieMap;

public interface UserMovieMapRepository extends JpaRepository<UserMovieMap, Long>{

	@Query( value = "Select u.mid from UserMovieMap u where u.uid = ?1 ")
	List<Long> getLikedMovieByUser(long userId);
}
