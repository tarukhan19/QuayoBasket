package com.project.quayobasket.Interface;


import com.project.quayobasket.Model.SubCategoryDTO;

public interface SubcategoryAdpaterListener {
    void onProductSelected(SubCategoryDTO detailDTO);

    void filterProduct(String query) ;
}
