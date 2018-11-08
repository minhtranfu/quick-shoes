/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MinhTran
 */
@Entity
@Table(name = "products", catalog = "timgiaynhanh", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p")
    , @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id")
    , @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name")
    , @NamedQuery(name = "Product.findByCategoryId", query = "SELECT p FROM Product p WHERE p.categoryId = :categoryId")
    , @NamedQuery(name = "Product.findBySex", query = "SELECT p FROM Product p WHERE p.sex = :sex")
    , @NamedQuery(name = "Product.findByPrice", query = "SELECT p FROM Product p WHERE p.price = :price")
    , @NamedQuery(name = "Product.findByOldPrice", query = "SELECT p FROM Product p WHERE p.oldPrice = :oldPrice")
    , @NamedQuery(name = "Product.findByImage", query = "SELECT p FROM Product p WHERE p.image = :image")
    , @NamedQuery(name = "Product.findByShopId", query = "SELECT p FROM Product p WHERE p.shopId = :shopId")
    , @NamedQuery(name = "Product.findByCode", query = "SELECT p FROM Product p WHERE p.code = :code")
    , @NamedQuery(name = "Product.findByShopIdAndCode", query = "SELECT p FROM Product p WHERE p.code = :code AND p.shopId = :shopId")
    , @NamedQuery(name = "Product.findBySourceUrl", query = "SELECT p FROM Product p WHERE p.sourceUrl = :sourceUrl")
    , @NamedQuery(name = "Product.findByManufactorId", query = "SELECT p FROM Product p WHERE p.manufactorId = :manufactorId")
    , @NamedQuery(name = "Product.findByMaterialId", query = "SELECT p FROM Product p WHERE p.materialId = :materialId")})
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", length = 255)
    private String name;
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(name = "sex")
    private Short sex;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price", precision = 22)
    private Double price;
    @Column(name = "old_price", precision = 22)
    private Double oldPrice;
    @Column(name = "image", length = 255)
    private String image;
    @Column(name = "shop_id")
    private Integer shopId;
    @Column(name = "code", length = 100)
    private String code;
    @Column(name = "source_url", length = 255)
    private String sourceUrl;
    @Column(name = "manufactor_id")
    private Integer manufactorId;
    @Column(name = "material_id")
    private Integer materialId;

    public Product() {
    }

    public Product(String name, Integer categoryId, Short sex, Double price,
            Double oldPrice, String image, Integer shopId, String code,
            String sourceUrl, Integer manufactor, Integer material) {
        
        this.name = name;
        this.categoryId = categoryId;
        this.sex = sex;
        this.price = price;
        this.oldPrice = oldPrice;
        this.image = image;
        this.shopId = shopId;
        this.code = code;
        this.sourceUrl = sourceUrl;
        this.manufactorId = manufactor;
        this.materialId = material;
    }

    public Product(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Integer getManufactorId() {
        return manufactorId;
    }

    public void setManufactorId(Integer manufactorId) {
        this.manufactorId = manufactorId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Product[ id=" + id + " ]";
    }
    
}
