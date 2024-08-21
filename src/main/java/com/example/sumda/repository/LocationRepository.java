package com.example.sumda.repository;

import com.example.sumda.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Locations,Long> {


    @Query(value = "SELECT * " +
            "FROM locations " +
            "ORDER BY SQRT(POW((Latitude - :latitude), 2) + POW((Longitude - :longitude), 2)) ASC " +
            "LIMIT 1",
            nativeQuery = true)
    Locations findNearestLocation(@Param("latitude") Double latitude, @Param("longitude") Double longitude);
}
