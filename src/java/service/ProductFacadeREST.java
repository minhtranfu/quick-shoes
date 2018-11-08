/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import constants.WebConstants;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import jpa.entities.Category;
import jpa.entities.Image;
import jpa.entities.Product;
import jpa.entities.ProductDetail;
import jpa.entities.ProductVersion;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
@javax.ejb.Stateless
@Path("products")
public class ProductFacadeREST extends AbstractFacade<Product> {

    @PersistenceContext(unitName = "ProjectFinalPU")
    private EntityManager em;

    public ProductFacadeREST() {
        super(Product.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Product entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Long id, Product entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Product find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Path("{id}/versions")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ProductVersion> detail(@PathParam("id") Long id) {
        return getEntityManager()
                .createNamedQuery("ProductVersion.findByProductId", ProductVersion.class)
                .setParameter("productId", id)
                .getResultList();
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Product> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Product> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        if (em == null) {
            em = DBUtils.getEntityManager();
        }

        return em;
    }
    
    @GET
    @Path("/allCategories")
    public List<Category> allCategories() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(Category.class));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    @GET
    @Path("/{id}/images")
    public List<Image> getProductImages(@PathParam("id") Integer productId) {
        return getEntityManager().createNamedQuery("Image.findByProductId", Image.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @GET
    @Path("/search")
    public List<Product> search(@QueryParam("manufactor") int manufactorId,
            @QueryParam("color") int colorId,
            @QueryParam("material") String materialId,
            @QueryParam("type") int categoryId,
            @QueryParam("size") int size,
            @QueryParam("sex") String sexStr
            ) {

        em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Product> c = cq.from(Product.class);

        cq.select(c);

        List<Predicate> predicates = new ArrayList<>();

        // filter manufactor
        if (manufactorId > 0) {
            predicates.add(cb.equal(c.get("manufactorId"), manufactorId));
        }

        // filter material
        if (materialId != null && !materialId.isEmpty()) {
            predicates.add(cb.like(c.get("materialId"), "%" + materialId + "%"));
        }

        // filter category
        if (categoryId > 0) {
            predicates.add(cb.equal(c.get("categoryId"), categoryId));
        }

        // filter sex
        if (sexStr != null && !sexStr.trim().isEmpty()) {
            try {
                short sex = Short.parseShort(sexStr);
                
                if (sex == 1 || sex == 0) {
                    predicates.add(cb.equal(c.get("sex"), sex));
                }
                
            } catch (Exception e) {
            }
        }

        // filter color and size
        if (colorId > 0 || size > 0) {
            // count books written by an author
            Subquery<ProductVersion> subquery = cq.subquery(ProductVersion.class);
            Root<ProductVersion> fromProductVersion = subquery.from(ProductVersion.class);
            subquery.select(fromProductVersion.get("productId")); // field to map with main-query
            List<Predicate> subPredicates = new ArrayList<>();
            
            if (colorId > 0) {
                subPredicates.add(cb.equal(fromProductVersion.get("colorId"), colorId));
            }
            
            if (size > 0) {
                subPredicates.add(cb.equal(fromProductVersion.get("size"), size));
            }
            
            subquery.where(subPredicates.toArray(new Predicate[]{}));

            predicates.add(cb.in(c.get("id")).value(subquery));
        }

        cq.where(predicates.toArray(new Predicate[]{}));
        Query q = em.createQuery(cq);

        return q.getResultList();
    }

}
