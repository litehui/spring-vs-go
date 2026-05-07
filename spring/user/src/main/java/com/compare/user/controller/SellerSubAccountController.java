package com.compare.user.controller;

import com.compare.core.result.R;
import com.compare.log.annotation.OperLog;
import com.compare.user.entity.SellerSubAccount;
import com.compare.user.service.SellerSubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seller/sub-account")
@RequiredArgsConstructor
public class SellerSubAccountController {

    private final SellerSubAccountService subAccountService;

    @GetMapping("/seller/{sellerId}")
    public R<List<SellerSubAccount>> listBySellerId(@PathVariable Long sellerId) {
        return R.ok(subAccountService.listBySellerId(sellerId));
    }

    @OperLog(value = "创建子账号", module = "user")
    @PostMapping
    public R<SellerSubAccount> create(@RequestBody SellerSubAccount subAccount) {
        return R.ok(subAccountService.create(subAccount));
    }

    @OperLog(value = "更新子账号", module = "user")
    @PutMapping("/{id}")
    public R<SellerSubAccount> update(@PathVariable Long id, @RequestBody SellerSubAccount subAccount) {
        return R.ok(subAccountService.update(id, subAccount));
    }

    @OperLog(value = "删除子账号", module = "user")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        subAccountService.delete(id);
        return R.ok();
    }
}
