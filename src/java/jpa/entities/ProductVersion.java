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
@Table(name = "product_versions", catalog = "timgiaynhanh", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductVersion.findAll", query = "SELECT p FROM ProductVersion p")
    , @NamedQuery(name = "ProductVersion.findById", query = "SELECT p FROM ProductVersion p WHERE p.id = :id")
    , @NamedQuery(name = "ProductVersion.findByProductId", query = "SELECT p FROM ProductVersion p WHERE p.productId = :productId ORDER BY p.size")
    , @NamedQuery(name = "ProductVersion.findByColorId", query = "SELECT p FROM ProductVersion p WHERE p.colorId = :colorId")
    , @NamedQuery(name = "ProductVersion.findByProductIdColorIdSize", query = "SELECT p FROM ProductVersion p WHERE p.productId = :productId AND p.colorId = :colorId AND p.size = :size")
    , @NamedQuery(name = "ProductVersion.findBySize", query = "SELECT p FROM ProductVersion p WHERE p.size = :size")})
public class ProductVersion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @Column(name = "product_id", nullable = false)
    private long productId;
    @Basic(optional = false)
    @Column(name = "color_id", nullable = false)
    private int colorId;
    @Basic(optional = false)
    @Column(name = "size", nullable = false)
    private float size;

    public ProductVersion() {
    }

    public ProductVersion(Long id) {
        this.id = id;
    }

    public ProductVersion(long productId, int colorId, float size) {
        this.productId = productId;
        this.colorId = colorId;
        this.size = size;
    }

    public ProductVersion(Long id, long productId, int colorId, float size) {
        this.id = id;
        this.productId = productId;
        this.colorId = colorId;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
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
        if (!(object instanceof ProductVersion)) {
            return false;
        }
        ProductVersion other = (ProductVersion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.ProductVersion[ id=" + id + " ]";
    }
    
}
