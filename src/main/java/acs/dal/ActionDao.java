package acs.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import acs.data.ActionEntity;

public interface ActionDao extends MongoRepository<ActionEntity, String> {

	// SELECT * FROM ELEMENTS WHERE NAME LIKE ?
	public List<ActionEntity> findOneByInvokedBy(@Param("invokedBy") String invokedBy, Pageable pageable);

	public List<ActionEntity> findOneByInvokedByAndTypeLike(@Param("invokedBy") String invokedBy,
			@Param("type") String type, Pageable pageable);

}
