package com.example.sumda.repository.station;

import com.example.sumda.entity.Station;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface StationRepository extends JpaRepository<Station, Long>, QuerydslPredicateExecutor<Station>, StationRepositoryCustom {
    @Query("SELECT s FROM Station s WHERE s.stationName LIKE %:name%")
    Slice<Station> findByStationNameContaining(@Param("name") String name, Pageable pageable);
}
