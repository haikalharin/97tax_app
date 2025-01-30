package com.repnox.nineseventax.features.productprices;

import java.util.List;

import com.repnox.nineseventax.features.models.ProductsList;

import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceRepo extends PagingAndSortingRepository<ProductsList, Long>, JpaSpecificationExecutor<ProductsList> {
    // @Query("Select products.* from ProductsList products")
    // List<Object[]> findAllProductPrices();

    @Query("from ProductsList where product = ?1")
    List<ProductsList> findByProductName(String product);
}
