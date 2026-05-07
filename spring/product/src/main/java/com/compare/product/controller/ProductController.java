package com.compare.product.controller;

import com.compare.core.result.R;
import com.compare.log.annotation.OperLog;
import com.compare.product.entity.Product;
import com.compare.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public R<Product> getById(@PathVariable Long id) {
        return R.ok(productService.getById(id));
    }

    @GetMapping("/seller/{sellerId}")
    public R<List<Product>> listBySellerId(@PathVariable Long sellerId) {
        return R.ok(productService.listBySellerId(sellerId));
    }

    @GetMapping("/shop/{shopId}")
    public R<List<Product>> listByShopId(@PathVariable Long shopId) {
        return R.ok(productService.listByShopId(shopId));
    }

    @GetMapping("/category/{category}")
    public R<List<Product>> listByCategory(@PathVariable String category) {
        return R.ok(productService.listByCategory(category));
    }

    @OperLog(value = "创建商品", module = "product")
    @PostMapping
    public R<Product> create(@RequestBody Product product) {
        return R.ok(productService.create(product));
    }

    @OperLog(value = "更新商品", module = "product")
    @PutMapping("/{id}")
    public R<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return R.ok(productService.update(id, product));
    }

    @OperLog(value = "删除商品", module = "product")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return R.ok();
    }

    @OperLog(value = "更新库存", module = "product")
    @PutMapping("/{id}/stock")
    public R<Product> updateStock(@PathVariable Long id, @RequestParam int quantity) {
        return R.ok(productService.updateStock(id, quantity));
    }
}
