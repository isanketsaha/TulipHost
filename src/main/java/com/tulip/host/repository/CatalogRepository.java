package com.tulip.host.repository;

import com.tulip.host.domain.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CatalogRepository extends JpaRepository<Catalog, Long>, QuerydslPredicateExecutor<Catalog> {}
