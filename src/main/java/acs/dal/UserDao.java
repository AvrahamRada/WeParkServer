package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acs.data.UserEntity;

@Repository
public interface UserDao extends MongoRepository<UserEntity,String>{ //extends PagingAndSortingRepository<UserEntity, String>{ 

		// SELECT * FROM USERS WHERE USERNAME LIKE ?
	// Is used in DatabaseActionService - in switch case: CREATE_USER_MANAGER_BY_USERNAME
		public List<UserEntity> findAllByUsernameOrUserId(
				@Param("username") String username,
				@Param("userId") String userId, 
								Pageable pageable);
		
//		// SELECT * FROM USERS WHERE USERNAME LIKE ?
//		public UserEntity findByUserId(
//				@Param("userId") String userId);
}
