package com.compare.favorite.controller;

import com.compare.core.result.R;
import com.compare.favorite.entity.FavoriteProduct;
import com.compare.favorite.service.FavoriteProductService;
import com.compare.log.annotation.OperLog;
import com.compare.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorite/product")
@RequiredArgsConstructor
public class FavoriteProductController {

    private final FavoriteProductService favoriteProductService;

    @GetMapping("/list")
    public R<List<FavoriteProduct>> list() {
        Long buyerId = SecurityContextUtil.getCurrentUserId();
        return R.ok(favoriteProductService.listByBuyerId(buyerId));
    }

    @OperLog(value = "收藏商品", module = "favorite")
    @PostMapping("/{productId}")
    public R<FavoriteProduct> add(@PathVariable Long productId) {
        Long buyerId = SecurityContextUtil.getCurrentUserId();
        return R.ok(favoriteProductService.add(buyerId, productId));
    }

    @OperLog(value = "取消收藏商品", module = "favorite")
    @DeleteMapping("/{productId}")
    public R<Void> remove(@PathVariable Long productId) {
        Long buyerId = SecurityContextUtil.getCurrentUserId();
        favoriteProductService.remove(buyerId, productId);
        return R.ok();
    }
}
