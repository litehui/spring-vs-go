package com.compare.user.controller;

import com.compare.core.result.R;
import com.compare.log.annotation.OperLog;
import com.compare.user.entity.Seller;
import com.compare.user.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/{id}")
    public R<Seller> getById(@PathVariable Long id) {
        return R.ok(sellerService.getById(id));
    }

    @OperLog(value = "更新卖家信息", module = "user")
    @PutMapping("/{id}")
    public R<Seller> update(@PathVariable Long id, @RequestBody Seller seller) {
        return R.ok(sellerService.update(id, seller));
    }

    @OperLog(value = "删除卖家", module = "user")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sellerService.delete(id);
        return R.ok();
    }
}
