/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author MinhTran
 * @param <T>
 * @param <PK>
 */
public interface IGenericDao<T, PK extends Serializable> {
    
    public T create(T t);
    
    public T get(PK id);
    
    public T update(T t);
    
    public boolean delete(T t);
    
    public List<T> getAll(String namedQuery);
}
