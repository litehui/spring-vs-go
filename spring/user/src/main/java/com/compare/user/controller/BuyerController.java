package com.compare.user.controller;

import com.compare.core.result.R;
import com.compare.log.annotation.OperLog;
import com.compare.user.entity.Buyer;
import com.compare.user.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @GetMapping("/{id}")
    public R<Buyer> getById(@PathVariable Long id) {
        return R.ok(buyerService.getById(id));
    }

    @OperLog(value = "更新买家信息", module = "user")
    @PutMapping("/{id}")
    public R<Buyer> update(@PathVariable Long id, @RequestBody Buyer buyer) {
        return R.ok(buyerService.update(id, buyer));
    }

    @OperLog(value = "删除买家", module = "user")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        buyerService.delete(id);
        return R.ok();
    }
}
