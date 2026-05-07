package com.compare.user.service;

import com.compare.core.exception.BusinessException;
import com.compare.user.entity.SellerSubAccount;
import com.compare.user.repository.SellerSubAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerSubAccountService {

    private final SellerSubAccountRepository subAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<SellerSubAccount> listBySellerId(Long sellerId) {
        return subAccountRepository.findBySellerId(sellerId);
    }

    public SellerSubAccount create(SellerSubAccount subAccount) {
        subAccount.setPassword(passwordEncoder.encode(subAccount.getPassword()));
        return subAccountRepository.save(subAccount);
    }

    public SellerSubAccount update(Long id, SellerSubAccount subAccount) {
        subAccountRepository.findById(id).orElseThrow(() -> new BusinessException("子账号不存在"));
        subAccount.setId(id);
        return subAccountRepository.save(subAccount);
    }

    public void delete(Long id) {
        subAccountRepository.deleteById(id);
    }
}
