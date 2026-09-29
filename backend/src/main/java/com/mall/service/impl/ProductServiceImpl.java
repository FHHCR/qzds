package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.BusinessException;
import com.mall.common.PageVO;
import com.mall.entity.dto.AdminProductQueryDTO;
import com.mall.entity.dto.CategoryDTO;
import com.mall.entity.dto.ProductDTO;
import com.mall.entity.dto.ProductQueryDTO;
import com.mall.entity.po.Category;
import com.mall.entity.po.Product;
import com.mall.mapper.CategoryMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现：分类、商品列表、商品详情。
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public ProductServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<Category> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    @Override
    public PageVO<Product> pageProducts(ProductQueryDTO query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1);
        if (query.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, query.getCategoryId());
        }
        // 关键词模糊匹配商品名
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Product::getName, query.getKeyword().trim());
        }
        // 排序
        String sort = query.getSort() == null ? "" : query.getSort();
        switch (sort) {
            case "price_asc" -> wrapper.orderByAsc(Product::getPrice);
            case "price_desc" -> wrapper.orderByDesc(Product::getPrice);
            default -> wrapper.orderByDesc(Product::getId);
        }
        return PageVO.of(productMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper));
    }

    @Override
    public Product getProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        return product;
    }

    // ==================== 管理端：商品 ====================

    @Override
    public PageVO<Product> adminPage(AdminProductQueryDTO query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Product::getName, query.getKeyword().trim());
        }
        wrapper.orderByDesc(Product::getId);
        return PageVO.of(productMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper));
    }

    @Override
    public void adminCreate(ProductDTO req) {
        Product product = new Product();
        product.setCategoryId(req.getCategoryId());
        product.setName(req.getName());
        product.setMainImage(req.getMainImage());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        product.setStatus(1);
        productMapper.insert(product);
    }

    @Override
    public void adminUpdate(Long id, ProductDTO req) {
        Product product = getProductById(id);
        product.setCategoryId(req.getCategoryId());
        product.setName(req.getName());
        product.setMainImage(req.getMainImage());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        productMapper.updateById(product);
    }

    @Override
    public void adminUpdateStatus(Long id, Integer status) {
        Product product = getProductById(id);
        product.setStatus(status == null || status != 1 ? 0 : 1);
        productMapper.updateById(product);
    }

    @Override
    public void adminDelete(Long id) {
        getProductById(id);
        productMapper.deleteById(id);
    }

    private Product getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return product;
    }

    // ==================== 管理端：分类 ====================

    @Override
    public List<Category> adminListCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    @Override
    public void adminCreateCategory(CategoryDTO req) {
        Category category = new Category();
        category.setName(req.getName());
        category.setSort(req.getSort() == null ? 0 : req.getSort());
        category.setStatus(1);
        categoryMapper.insert(category);
    }

    @Override
    public void adminUpdateCategory(Long id, CategoryDTO req) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        category.setName(req.getName());
        category.setSort(req.getSort() == null ? 0 : req.getSort());
        categoryMapper.updateById(category);
    }

    @Override
    public void adminDeleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        Long count = productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, id));
        if (count > 0) {
            throw new BusinessException(400, "该分类下还有商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
