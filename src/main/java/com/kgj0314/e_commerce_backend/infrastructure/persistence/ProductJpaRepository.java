package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Query(""" 
        select p from Product p
        join fetch p.stock
        order by p.createdDate desc
    """)
    Page<Product> findAllFetchJoin(Pageable pageable);

    @Query("select p from Product p join fetch p.stock where p.id = :id")
    Product findByIdFetchJoin(@Param("id") Long id);
}
