package com.compare.user.repository;

import com.compare.user.entity.SellerSubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SellerSubAccountRepository extends JpaRepository<SellerSubAccount, Long> {
    List<SellerSubAccount> findBySellerId(Long sellerId);
}
