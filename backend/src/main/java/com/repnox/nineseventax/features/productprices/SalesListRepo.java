package com.repnox.nineseventax.features.productprices;

import java.util.List;

import com.repnox.nineseventax.features.models.SalesList;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SalesListRepo extends PagingAndSortingRepository<SalesList, Long>, JpaSpecificationExecutor<SalesList> {
    // @Query("Select products.* from ProductsList products")
    // List<Object[]> findAllProductPrices();

}
