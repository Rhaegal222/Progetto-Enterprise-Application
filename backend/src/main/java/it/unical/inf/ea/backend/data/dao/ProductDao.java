package it.unical.inf.ea.backend.data.dao;

import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.dto.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends JpaRepository<Product,String>, JpaSpecificationExecutor<Product> {
    Page<Product> findAllBySellerAndVisibilityEquals(User user, Visibility visibility,Pageable pageable);
    Page<Product> findAllBySeller(User user,Pageable pageable);

    Page<Product> findAllByTitleContainingOrDescriptionContainingAndVisibility(String title, String description,Visibility visibility,Pageable pageable);

    @Query("select p from Product p where p.productCost.price between :start and :end and p.visibility=:visibility" )
    Page<Product> getByProductByPrice(@Param("start") Double startPrice,@Param("end") Double endPrice,@Param("visibility")Visibility visibility, Pageable pageable);

    Page<Product> findAllByVisibility(Visibility visibility, Pageable pageable);
    Page<Product> findAllByVisibilityAndUsersThatLiked(Visibility visibility,User user,Pageable pageable);


    @Query("update Product p set p.likesNumber = p.likesNumber + 1 where p.id = ?1")
    void increaseLikesNumber(String productId);

    @Query("update Product p set p.likesNumber = p.likesNumber - 1 where p.id = ?1")
    void decreaseLikesNumber(String productId);

    Page<Product> findAllByUsersThatLiked(User user, Pageable pageable);
    //@Query(value = "select u from users u, user_likes ulp where ulp.product_id = ?1 and ulp.user_id = u.id", nativeQuery = true)
    //Page<User> findAllUsersThatLikedProduct(String productId, Pageable pageable);

}
