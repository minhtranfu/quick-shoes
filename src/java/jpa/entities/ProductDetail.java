/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.util.List;

/**
 *
 * @author MinhTran
 */
public class ProductDetail extends Product {
    private List<ProductVersion> productVersions;
    private String manufactor;
    private String material;

    public List<ProductVersion> getProductVersions() {
        return productVersions;
    }

    public void setProductVersions(List<ProductVersion> productVersions) {
        this.productVersions = productVersions;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
    
    
}
