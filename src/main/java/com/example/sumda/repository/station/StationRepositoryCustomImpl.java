package com.example.sumda.repository.station;

import com.example.sumda.entity.QStation;
import org.springframework.stereotype.Repository;

@Repository
public class StationRepositoryCustomImpl implements StationRepositoryCustom {
    QStation qStation = QStation.station;
}
