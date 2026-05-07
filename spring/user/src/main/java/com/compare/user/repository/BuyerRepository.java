package com.compare.user.repository;

import com.compare.user.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
