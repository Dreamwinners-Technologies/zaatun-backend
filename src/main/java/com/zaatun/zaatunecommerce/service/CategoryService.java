package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.CategoryAddEditRequest;
import com.zaatun.zaatunecommerce.dto.request.SubCategoryAddEditRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.CategoryRepository;
import com.zaatun.zaatunecommerce.repository.SubCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final JwtProvider jwtProvider;
    private final UtilService utilService;

    public ResponseEntity<ApiResponse<String>> addCategory(String token, CategoryAddEditRequest categoryAddEditRequest) {
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo(categoryAddEditRequest.getCategoryName(), token);

        CategoryModel categoryModel = new CategoryModel(basicTableInfo.getId(), basicTableInfo.getCreationTime(),
                basicTableInfo.getCreateBy(), null, null, categoryAddEditRequest.getCategoryName(), categoryAddEditRequest.getCategoryIcon(),
                basicTableInfo.getSlug(), null, null, null);

        categoryRepository.save(categoryModel);

        return new ResponseEntity<>(new ApiResponse<>(201, "Category Created", basicTableInfo.getId()),
                HttpStatus.CREATED);
    }


    public ResponseEntity<ApiResponse<String>> addSubCategory(String token, SubCategoryAddEditRequest subCategoryAddEditRequest,
                                                              String categoryId) {

        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();

            BasicTableInfo basicTableInfo =
                    utilService.generateBasicTableInfo(subCategoryAddEditRequest.getSubCategoryName(), token);

            SubCategoryModel subCategoryModel = new SubCategoryModel(basicTableInfo.getId(), basicTableInfo.getCreationTime(),
                    basicTableInfo.getCreateBy(), null, null, subCategoryAddEditRequest.getSubCategoryName(),
                    subCategoryAddEditRequest.getSubCategoryIcon(), basicTableInfo.getSlug(), null);

            List<SubCategoryModel> subCategoryModels = categoryModel.getSubCategories();
            subCategoryModels.add(subCategoryModel);

            categoryRepository.save(categoryModel);

            return new ResponseEntity<>(new ApiResponse<>(201, "Sub Category Created", basicTableInfo.getId()),
                    HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }

    }

    public ResponseEntity<ApiResponse<List<CategoryModel>>> getCategoriesList() {
        List<CategoryModel> categoryModelList = categoryRepository.findAll();

        if (categoryModelList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Category Found", categoryModelList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Found All Categories", categoryModelList), HttpStatus.OK);
        }

    }

    public ResponseEntity<ApiResponse<String>> editCategory(String token, CategoryAddEditRequest categoryAddEditRequest, String categoryId) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();

            categoryModel.setCategoryName(categoryAddEditRequest.getCategoryName());
            categoryModel.setCategoryIcon(categoryAddEditRequest.getCategoryIcon());
            categoryModel.setUpdateBy(jwtProvider.getNameFromJwt(token));
            categoryModel.setUpdateTime(System.currentTimeMillis());

            categoryRepository.save(categoryModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Category Edit Successful", categoryModel.getCategoryId()),
                    HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }

    public ResponseEntity<ApiResponse<String>> editSubCategory(String token, SubCategoryAddEditRequest subCategoryAddEditRequest,
                                                               String categoryId, String subCategoryId) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();
            Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(subCategoryId);

            if (subCategoryModelOptional.isPresent()) {
                SubCategoryModel subCategoryModel = subCategoryModelOptional.get();

                if (categoryModel.getSubCategories().contains(subCategoryModel)) {
                    subCategoryModel.setSubCategoryName(subCategoryAddEditRequest.getSubCategoryName());
                    subCategoryModel.setSubCategoryIcon(subCategoryAddEditRequest.getSubCategoryIcon());
                    subCategoryModel.setUpdateBy(jwtProvider.getNameFromJwt(token));
                    subCategoryModel.setUpdateTime(System.currentTimeMillis());

                    subCategoryRepository.save(subCategoryModel);

                    return new ResponseEntity<>(new ApiResponse<>(200, "Sub Category Edit SuccessFul",
                            subCategoryModel.getSubCategoryId()), HttpStatus.OK);
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subcategory is not under this category");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SubCategory found with this Sub Category Id");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }

    }

    public ResponseEntity<ApiResponse<String>> deleteCategory(String categoryId) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            categoryRepository.deleteById(categoryId);

            return new ResponseEntity<>(new ApiResponse<>(200, "Category Delete SuccessFul",
                    categoryModelOptional.get().getCategoryId()), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteSubCategory(String token, String categoryId, String subCategoryId) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();
            Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(subCategoryId);

            List<SubCategoryModel> subCategoryModels = categoryModel.getSubCategories();
            if (subCategoryModelOptional.isPresent()) {
                SubCategoryModel subCategoryModel = subCategoryModelOptional.get();

                if (subCategoryModels.contains(subCategoryModel)) {
                    subCategoryModels.remove(subCategoryModel);

                    categoryRepository.save(categoryModel);
                    subCategoryRepository.deleteById(subCategoryId);
                    return new ResponseEntity<>(new ApiResponse<>(200, "SubCategory Delete SuccessFul",
                            subCategoryId), HttpStatus.OK);

                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subcategory is not under this category");
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SubCategory found with this sub category Id");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }
}
