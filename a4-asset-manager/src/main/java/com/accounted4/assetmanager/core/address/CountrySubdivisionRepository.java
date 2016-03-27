package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.entity.CountrySubdivision;
import com.accounted4.assetmanager.entity.Country;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface CountrySubdivisionRepository extends JpaRepository<CountrySubdivision, Long>{

    List<CountrySubdivision> findByCountryOrderBySubdivisionCode(Country country);
}
