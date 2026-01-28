package kr.hhplus.be.server.product.infrastructure.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

//@Repository
public class ProductPopularQuery {

    private final JPAQueryFactory query;

    public ProductPopularQuery(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }


}
