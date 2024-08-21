package com.example.sumda.repository;

import com.example.sumda.entity.Locations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Locations,Long> {


    @Query(value = "SELECT * " +
            "FROM locations " +
            "ORDER BY SQRT(POW((Latitude - :latitude), 2) + POW((Longitude - :longitude), 2)) ASC " +
            "LIMIT 1",
            nativeQuery = true)
    Locations findNearestLocation(@Param("latitude") Double latitude, @Param("longitude") Double longitude);

    @Query("SELECT l FROM Locations l where l.district like %:name%")
    Slice<Locations> findByLocationNameContaining(@Param("name") String name, Pageable pageable);

}
