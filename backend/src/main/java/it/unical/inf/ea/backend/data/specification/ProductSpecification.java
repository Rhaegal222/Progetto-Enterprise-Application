package it.unical.inf.ea.backend.data.specification;

import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.dto.enums.*;
import jakarta.persistence.criteria.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductSpecification {

    @Data
    public static class Filter {
        private final UserDao userDao;

        private String title;
        private String description;
        private Double minProductCost;
        private Double maxProductCost;
        private List<String> brands;
        private LocalDateTime uploadDate;
        private Availability availability;
        private ProductCategory productCategory;
        private String primaryCat;
        private String secondaryCat;
        private String tertiaryCat;
        private User seller;
        private AlcoholicType alcoholicType;
        private SavoryType savoryType;
        private SweetType sweetType;

        public void setSeller(String userID) {
            if (userID != null) {
                Optional<User> user = userDao.findById(userID);
                this.seller = user.get();
            }
        }

    }

    public static Specification<Product> withFilters(Filter filter) {
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                try {
                    List<Predicate> predicates = new ArrayList<>();


                    List<Predicate> likePredicates = new ArrayList<>();
                    if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                        likePredicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                '%' + filter.getTitle().toLowerCase() + '%')
                        );
                    }
                    if (filter.getDescription() != null && !filter.getDescription().isEmpty()) {
                        likePredicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("description")),
                                '%' + filter.getDescription().toLowerCase() + '%')
                        );
                    }
                    if (filter.getBrands() != null && !filter.getBrands().isEmpty()) {
                        likePredicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("brand")),
                                '%' + filter.getBrands().get(0).toLowerCase() + '%')
                        );
                    }

                    if (!likePredicates.isEmpty()) {
                        predicates.add(criteriaBuilder.or(likePredicates.toArray(new Predicate[0])));
                    }


                    if (filter.getMinProductCost() != null && filter.getMaxProductCost() != null) {
                        predicates.add(criteriaBuilder.between(root.get("productCost").get("price"), filter.getMinProductCost(), filter.getMaxProductCost()));
                    }
                    else if (filter.getMinProductCost() != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("productCost").get("price"), filter.getMinProductCost()));
                    }
                    else if (filter.getMaxProductCost() != null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("productCost").get("price"), filter.getMaxProductCost()));
                    }

                    if (filter.getBrands() != null && !filter.getBrands().isEmpty()) {
                        predicates.add(root.get("brand").in(filter.getBrands()));
                    }


                    if (filter.getUploadDate() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("uploadDate"), filter.getUploadDate()));
                    }

                    predicates.add(criteriaBuilder.equal(root.get("availability"), Availability.AVAILABLE));

                    if (filter.getProductCategory() != null)
                        predicates.add(criteriaBuilder.equal(root.get("productCategory"), filter.getProductCategory()));

                    if (filter.getPrimaryCat() != null && !filter.getPrimaryCat().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("productCategory").get("primaryCat"), filter.getPrimaryCat()));
                    }

                    if (filter.getSecondaryCat() != null && !filter.getSecondaryCat().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("productCategory").get("secondaryCat"), filter.getSecondaryCat()));
                    }

                    if (filter.getTertiaryCat() != null && !filter.getTertiaryCat().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("productCategory").get("tertiaryCat"), filter.getTertiaryCat()));
                    }

                    if (filter.getSeller() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("seller"), filter.getSeller()));
                    }
                    predicates.add(criteriaBuilder.equal(root.get("visibility"), Visibility.PUBLIC));

                    //ATTRIBUTI DELLA CLASSE Alcoholic.class
                    if (filter.getPrimaryCat() != null && filter.getPrimaryCat().equals("Alcoholic")) {
                        Root<Alcoholic> alcoholicRoot = query.from(Alcoholic.class);
                        predicates.add(criteriaBuilder.equal(root.get("id"), alcoholicRoot.get("id")));

                        if (filter.getAlcoholicType() != null) {
                            predicates.add(criteriaBuilder.equal(alcoholicRoot.get("alcoholicType"), filter.getAlcoholicType()));
                        }

                    }

                    //ATTRIBUTO DELLA CLASSE Sweet.class
                    if (filter.getPrimaryCat() != null && filter.getPrimaryCat().equals("Sweet")) {
                        Root<Sweet> sweetRoot = query.from(Sweet.class);
                        predicates.add(criteriaBuilder.equal(root.get("id"), sweetRoot.get("id")));

                        if (filter.getSweetType() != null) {
                            predicates.add(criteriaBuilder.equal(sweetRoot.get("sweetType"), filter.getSweetType()));
                        }

                    }

                    //ATTRIBUTI SPECIFICI DELLA CLASSE Savory.class
                    if (filter.getPrimaryCat() != null && filter.getPrimaryCat().equals("Savory")) {
                        Root<Savory> savoryRoot = query.from(Savory.class);
                        predicates.add(criteriaBuilder.equal(root.get("id"), savoryRoot.get("id")));

                        if (filter.getSavoryType() != null) {
                            predicates.add(criteriaBuilder.equal(savoryRoot.get("savoryType"), filter.getSavoryType()));
                        }
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        };
    }
}
