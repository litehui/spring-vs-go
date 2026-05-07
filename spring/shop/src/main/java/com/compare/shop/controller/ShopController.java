package com.compare.shop.controller;

import com.compare.core.result.R;
import com.compare.log.annotation.OperLog;
import com.compare.shop.entity.Shop;
import com.compare.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/{id}")
    public R<Shop> getById(@PathVariable Long id) {
        return R.ok(shopService.getById(id));
    }

    @GetMapping("/seller/{sellerId}")
    public R<List<Shop>> listBySellerId(@PathVariable Long sellerId) {
        return R.ok(shopService.listBySellerId(sellerId));
    }

    @OperLog(value = "创建店铺", module = "shop")
    @PostMapping
    public R<Shop> create(@RequestBody Shop shop) {
        return R.ok(shopService.create(shop));
    }

    @OperLog(value = "更新店铺", module = "shop")
    @PutMapping("/{id}")
    public R<Shop> update(@PathVariable Long id, @RequestBody Shop shop) {
        return R.ok(shopService.update(id, shop));
    }

    @OperLog(value = "删除店铺", module = "shop")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        shopService.delete(id);
        return R.ok();
    }
}
