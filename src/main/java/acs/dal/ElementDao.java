package acs.dal;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import acs.data.ElementEntity;
import acs.util.Location;

public interface ElementDao extends MongoRepository<ElementEntity,String>{ //extends PagingAndSortingRepository<ElementEntity, String> {

	// SELECT * FROM ELEMENTS WHERE NAME LIKE ?
	public List<ElementEntity> findAllByNameLike(
			 @Param("name") String name, 
							Pageable pageable);
	
	// SELECT * FROM ELEMENTS WHERE NAME LIKE AND ACTIVE IS TRUE ?
	public List<ElementEntity> findAllByNameLikeAndActive(
			@Param("name") String name,
			@Param("active") Boolean active, 
							Pageable pageable);
	
	// SELECT * FROM ELEMENTS WHERE Type LIKE ?
	public List<ElementEntity> findAllByTypeLike(
			@Param("type") String type, 
							Pageable pageable);
	
	// SELECT * FROM ELEMENTS WHERE Type LIKE AND ACTIVE IS TRUE ?
		public List<ElementEntity> findAllByTypeLikeAndActive(
				@Param("type") String type,
				@Param("active") Boolean active, 
								Pageable pageable);
	
//	// SELECT * FROM ELEMENTS WHERE ... LIKE ?
//	public List<ElementEntity> findAllByLocation_LatBetweenAndLocation_LngBetween(
//			@Param("minLat") Double minLat,
//			@Param("maxLat") Double maxLat,
//			@Param("minLng") Double minLng,
//			@Param("maxLng") Double maxLng, 
//			Pageable pageable);
//	
//	// SELECT * FROM ELEMENTS WHERE ... LIKE AND ACTIVE IS TRUE ?
//	public List<ElementEntity> findAllByLocation_LatBetweenAndLocation_LngBetweenAndActive(
//			@Param("minLat") Double minLat,
//			@Param("maxLat") Double maxLat,
//			@Param("minLng") Double minLng,
//			@Param("maxLng") Double maxLng,
//			@Param("active") Boolean active, 
//			Pageable pageable);
	
	
	// SELECT * FROM ELEMENTS WHERE ACTIVE IS TRUE ?
		public List<ElementEntity> findAllByActive(
				@Param("active") Boolean active,
								Pageable pageable);
		
	// SELECT * FROM ELEMENTS WHERE ELEMENT_ID LIKE AND ACTIVE IS TRUE ?
		public List<ElementEntity> findOneByElementIdAndActive(
				@Param("elementId") String elementId,
				@Param("active") Boolean active,
								Pageable pageable);
		
//		public List<ElementEntity> findAllByOrigin(
//				@Param("origin") ElementEntity origin,
//								 Pageable pageable);
//		
//		public List<ElementEntity> findAllByOriginAndActive(
//				@Param("origin") ElementEntity origin,
//				@Param("active") Boolean active,
//								Pageable pageable);
}
