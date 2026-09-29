package com.mall.service;

import com.mall.common.PageVO;
import com.mall.entity.dto.AdminProductQueryDTO;
import com.mall.entity.dto.CategoryDTO;
import com.mall.entity.dto.ProductDTO;
import com.mall.entity.dto.ProductQueryDTO;
import com.mall.entity.po.Category;
import com.mall.entity.po.Product;

import java.util.List;

/**
 * 商品服务：分类、商品列表、商品详情，以及管理端商品/分类维护。
 */
public interface ProductService {

    List<Category> listCategories();

    PageVO<Product> pageProducts(ProductQueryDTO query);

    Product getProduct(Long id);

    /** 管理端：商品分页（含下架） */
    PageVO<Product> adminPage(AdminProductQueryDTO query);

    /** 管理端：新增商品 */
    void adminCreate(ProductDTO req);

    /** 管理端：修改商品 */
    void adminUpdate(Long id, ProductDTO req);

    /** 管理端：上架/下架 */
    void adminUpdateStatus(Long id, Integer status);

    /** 管理端：删除商品（逻辑删除） */
    void adminDelete(Long id);

    /** 管理端：分类列表（含禁用） */
    List<Category> adminListCategories();

    /** 管理端：新增分类 */
    void adminCreateCategory(CategoryDTO req);

    /** 管理端：修改分类 */
    void adminUpdateCategory(Long id, CategoryDTO req);

    /** 管理端：删除分类（分类下有商品时禁止删除） */
    void adminDeleteCategory(Long id);
}
