package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

//import acs.action.InvokedBy;
import acs.data.ActionEntity;
import acs.data.ElementEntity;

public interface ActionDao extends MongoRepository<ActionEntity, String> {

	// SELECT * FROM ELEMENTS WHERE NAME LIKE ?
//	public List<ActionEntity> findFirstByOrderByCreatedTimestampDescAndInvokedBy(@Param("invokedBy") String invokedBy, Pageable pageable);

	// Maybe this one will work?
//	public List<ActionEntity> findOneByInvokedByAndTopByOrderByCreatedTimestampDesc(@Param("invokedBy") String invokedBy, Pageable pageable);

	// SELECT * FROM ELEMENTS WHERE NAME LIKE ?
	public List<ActionEntity> findOneByInvokedBy(@Param("invokedBy") String invokedBy, Pageable pageable);

	public List<ActionEntity> findOneByInvokedByAndTypeLike(@Param("invokedBy") String invokedBy,
			@Param("type") String type, Pageable pageable);

}
