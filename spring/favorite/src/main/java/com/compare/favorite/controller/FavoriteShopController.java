package com.compare.favorite.controller;

import com.compare.core.result.R;
import com.compare.favorite.entity.FavoriteShop;
import com.compare.favorite.service.FavoriteShopService;
import com.compare.log.annotation.OperLog;
import com.compare.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorite/shop")
@RequiredArgsConstructor
public class FavoriteShopController {

    private final FavoriteShopService favoriteShopService;

    @GetMapping("/list")
    public R<List<FavoriteShop>> list() {
        Long buyerId = SecurityContextUtil.getCurrentUserId();
        return R.ok(favoriteShopService.listByBuyerId(buyerId));
    }

    @OperLog(value = "收藏店铺", module = "favorite")
    @PostMapping("/{shopId}")
    public R<FavoriteShop> add(@PathVariable Long shopId) {
        Long buyerId = SecurityContextUtil.getCurrentUserId();
        return R.ok(favoriteShopService.add(buyerId, shopId));
    }

    @OperLog(value = "取消收藏店铺", module = "favorite")
    @DeleteMapping("/{shopId}")
    public R<Void> remove(@PathVariable Long shopId) {
        Long buyerId = SecurityContextUtil.getCurrentUserId();
        favoriteShopService.remove(buyerId, shopId);
        return R.ok();
    }
}
