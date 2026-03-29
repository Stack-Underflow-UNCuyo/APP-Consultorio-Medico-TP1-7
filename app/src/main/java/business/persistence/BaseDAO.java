package business.persistence;

import java.util.List;


public interface BaseDAO<T> {
    // create
    long insert(T entity);

    // Read
    T getById(long id);
    List<T> getAll();

    // update
    int update(T entity);

    // Delete
    boolean delete(long id);

}
