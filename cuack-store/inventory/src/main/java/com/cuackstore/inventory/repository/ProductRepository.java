package com.cuackstore.inventory.repository;

import com.cuackstore.inventory.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    @Query("SELECT * FROM products WHERE hawa = :hawa")
    Mono<Product> findByHawa(@Param("hawa") String hawa);

    @Query("SELECT * FROM products WHERE available = 1 AND stock > 0")
    Flux<Product> findAvailableProducts();

    @Query("SELECT COUNT(*) FROM products WHERE hawa = :hawa")
    Mono<Long> countByHawa(@Param("hawa") String hawa);

    @Query("UPDATE products SET stock = :stock WHERE hawa = :hawa")
    Mono<Integer> updateStockByHawa(@Param("hawa") String hawa, @Param("stock") Integer stock);

    @Query("UPDATE products SET stock = stock + :quantity WHERE hawa = :hawa")
    Mono<Integer> incrementStock(@Param("hawa") String hawa, @Param("quantity") Integer quantity);

    @Query("UPDATE products SET stock = stock - :quantity WHERE hawa = :hawa AND stock >= :quantity")
    Mono<Integer> decrementStock(@Param("hawa") String hawa, @Param("quantity") Integer quantity);

    @Query("SELECT * FROM products WHERE stock <= :threshold AND available = 1")
    Flux<Product> findProductsWithLowStock(@Param("threshold") Integer threshold);

    @Query("UPDATE products SET available = :available WHERE hawa = :hawa")
    Mono<Integer> updateAvailabilityByHawa(@Param("hawa") String hawa, @Param("available") Boolean available);
}
