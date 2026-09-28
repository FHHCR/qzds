package com.mall.service;

import com.mall.common.PageVO;
import com.mall.entity.dto.ProductQueryDTO;
import com.mall.entity.po.Category;
import com.mall.entity.po.Product;

import java.util.List;

/**
 * 商品服务：分类、商品列表、商品详情。
 */
public interface ProductService {

    List<Category> listCategories();

    PageVO<Product> pageProducts(ProductQueryDTO query);

    Product getProduct(Long id);
}
