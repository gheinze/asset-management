package com.accounted4.assetmanager.core.address;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface CountrySubdivisionRepository extends JpaRepository<CountrySubdivision, Long>{

    List<CountrySubdivision> findByCountryOrderBySubdivisionCode(Country country);
}
