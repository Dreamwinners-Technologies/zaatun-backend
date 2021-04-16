package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.response.shop.*;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.ProductModel;
import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ShopProductHelperService {
    private final ProductRepository productRepository;

    public List<ShopProductResponse> shopProductResponseFromProductsDb(List<ProductModel> productModels) {
        List<ShopProductResponse> shopProductResponses = new ArrayList<>();
        for (ProductModel productModel : productModels) {

            CategoryModel categoryModel = productModel.getCategoryModel();
            List<ShopSubCategoryResponse> shopSubCategoryResponses = new ArrayList<>();
            ShopCategoryResponse shopCategoryResponse = new ShopCategoryResponse(categoryModel.getCategoryName(),
                    categoryModel.getCategoryIcon(), categoryModel.getCategorySlug(), categoryModel.getCategoryImage(),
                    shopSubCategoryResponses);

            SubCategoryModel subCategoryModel = productModel.getSubCategoryModel();
            ShopSubCategoryResponse shopSubCategoryResponse = new ShopSubCategoryResponse(
                    subCategoryModel.getSubCategoryName(), subCategoryModel.getSubCategoryIcon(),
                    subCategoryModel.getSubCategorySlug(), subCategoryModel.getSubCategoryImage());

            ShopProductResponse shopProductResponse = new ShopProductResponse(productModel.getProductName(),
                    productModel.getProductSlug(), productModel.getSKU(), productModel.getBrand(), shopCategoryResponse,
                    shopSubCategoryResponse, productModel.getRegularPrice(), productModel.getDiscountPrice(),
                    productModel.getDescription(), productModel.getShortDescription(), productModel.getWarranty(),
                    productModel.getEmi(), productModel.getInStock(), productModel.getIsFeatured(),
                    productModel.getIsAvailable(), productModel.getVideoUrl(), productModel.getAffiliatePercentage(),
                    productModel.getVat(), productModel.getProductImages(), productModel.getVariants(),
                    productModel.getProductReviews(), productModel.getProcessor(), productModel.getBattery(),
                    productModel.getRam(), productModel.getRom(), productModel.getScreenSize(),
                    productModel.getBackCamera(), productModel.getFrontCamera());

            shopProductResponses.add(shopProductResponse);
        }
        return shopProductResponses;
    }

    public List<ShopOrderProductResponse> shopOrderProductResponseFromProducts(List<ProductModel> productModels) {
        List<ShopOrderProductResponse> shopOrderProductResponses = new ArrayList<>();
        for (ProductModel productModel : productModels) {

            ProductVariantModel productVariantModel = productModel.getVariants().get(0);
            ShopOrderVariantResponse shopOrderVariantResponse =
                    new ShopOrderVariantResponse(productVariantModel.getVariant(), productVariantModel.getQuantity());

            ShopOrderProductResponse shopOrderProductResponse = new ShopOrderProductResponse(productModel.getProductName(),
                    productModel.getProductSlug(), productModel.getSKU(), productModel.getBrand(),
                    productModel.getCategoryModel().getCategoryName() , productModel.getSubCategoryModel().getSubCategoryName(),
                    productModel.getRegularPrice(), productModel.getDiscountPrice(), productModel.getWarranty(),
                    productModel.getEmi(), productModel.getVat(), productModel.getProductImages(), shopOrderVariantResponse);

            shopOrderProductResponses.add(shopOrderProductResponse);
        }
        return shopOrderProductResponses;
    }
}
