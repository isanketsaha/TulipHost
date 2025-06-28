package com.tulip.host.repository;


import com.tulip.host.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

    @Repository
    public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {

        Optional<Coupon> findByCode(String code);

        @Query("SELECT c FROM Coupon c " +
                "WHERE c.isActive = true ")
        Optional<List<Coupon>> findAllActive();

        @Query("SELECT c FROM Coupon c " +
            "WHERE c.code = :code AND c.isActive = true " +
            "AND c.startDate <= :now AND c.endDate >= :now " +
            "AND (SELECT COUNT(t) FROM Transaction t WHERE t.couponId.id = c.id) < c.usageLimit")
        Optional<Coupon> findValidCoupon(@Param("code") String code, @Param("now") LocalDateTime now);
    }

