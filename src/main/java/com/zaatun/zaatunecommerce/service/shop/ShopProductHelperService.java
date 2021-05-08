package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.response.shop.*;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
//Helper class for Store Product logics
public class ShopProductHelperService {
    private final ProductRepository productRepository;

    //Migration from ProductModel List to ShopProductResponse List for Store.
    public List<ShopProductResponse> shopProductResponseFromProductsDb(List<ProductModel> productModels) {
        //New ArrayList of ShopProductResponse
        List<ShopProductResponse> shopProductResponses = new ArrayList<>();

        //Iterating the ProductModel and migrating to shopProductResponse then add to the ArrayList
        for (ProductModel productModel : productModels) {

            ShopProductResponse shopProductResponse = migrateProductModelToShopProductResponse(productModel);

            //Adding the ShopProductResponse to the list
            shopProductResponses.add(shopProductResponse);
        }

        //Returning the list of ShopProductResponse
        return shopProductResponses;
    }

    public ShopProductResponse migrateProductModelToShopProductResponse(ProductModel productModel) {
        //Getting the CategoryModel from the ProductModel
        CategoryModel categoryModel = productModel.getCategoryModel();

        //Migrating from CategoryModel to ShopCategoryResponse
        ShopCategoryResponse shopCategoryResponse = new ShopCategoryResponse(
                categoryModel.getCategoryName(), categoryModel.getCategoryIcon(),
                categoryModel.getCategorySlug(), categoryModel.getCategoryImage(),
                 categoryModel.getVerticalImage(),null);

        //Getting the SubCategoryModel from the ProductModel
        SubCategoryModel subCategoryModel = productModel.getSubCategoryModel();

        //Migrating from SubCategoryModel to ShopSubCategoryResponse
        ShopSubCategoryResponse shopSubCategoryResponse = new ShopSubCategoryResponse(
                subCategoryModel.getSubCategoryName(), subCategoryModel.getSubCategoryIcon(),
                subCategoryModel.getSubCategorySlug(), subCategoryModel.getSubCategoryImage());

        List<ShopVariationResponse> shopVariationResponses = new ArrayList<>();

        for (ProductVariationModel productVariationModel: productModel.getVariations()){
            ShopVariationResponse shopVariationResponse = new ShopVariationResponse(productVariationModel.getId(),
                    productVariationModel.getStock(), productVariationModel.getInStock(), productVariationModel.getIsDefault(),
                    productVariationModel.getRegularPrice(), productVariationModel.getDiscountPrice(),
                    productVariationModel.getAttributeCombinations());

            shopVariationResponses.add(shopVariationResponse);
        }

        //Adding Data to ShopProductResponse from ProductModel
        ShopProductResponse shopProductResponse = new ShopProductResponse(productModel.getProductName(),
                productModel.getProductSlug(), productModel.getProductBadge(), productModel.getSKU(), productModel.getBrand(), shopCategoryResponse,
                shopSubCategoryResponse, productModel.getDescription(), productModel.getShortDescription(), productModel.getWarranty(),
                productModel.getEmi(), productModel.getInStock(), productModel.getIsFeatured(),
                productModel.getIsDiscount(), productModel.getVideoUrl(), productModel.getVat(),
                productModel.getProductImages(), productModel.getProductReviews(),
                productModel.getBuyersId(), productModel.getSpecification(), null, productModel.getProductAttributeModels(),
                shopVariationResponses);
        return shopProductResponse;
    }

//    public List<ShopOrderProductResponse> shopOrderProductResponseFromProducts(List<ProductModel> productModels) {
//        List<ShopOrderProductResponse> shopOrderProductResponses = new ArrayList<>();
//        for (ProductModel productModel : productModels) {

//            ProductVariantModel productVariantModel = productModel.getVariants().get(0);
//            ShopOrderVariantResponse shopOrderVariantResponse =
//                    new ShopOrderVariantResponse(productVariantModel.getVariant(), productVariantModel.getQuantity());
//
//            ShopOrderProductResponse shopOrderProductResponse = new ShopOrderProductResponse(productModel.getProductName(),
//                    productModel.getProductSlug(), productModel.getSKU(), productModel.getBrand(),
//                    productModel.getCategoryModel().getCategoryName() , productModel.getSubCategoryModel().getSubCategoryName(),
//                    productModel.getRegularPrice(), productModel.getDiscountPrice(), productModel.getWarranty(),
//                    productModel.getEmi(), productModel.getVat(), productModel.getProductImages(), null);
//
//            shopOrderProductResponses.add(shopOrderProductResponse);
//        }
//        return shopOrderProductResponses;
//    }
}
