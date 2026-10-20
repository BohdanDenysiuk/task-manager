package com.bohdandenysiuk.taskmanager;

import java.util.Comparator;
import java.util.List;

public interface Repository<T> {

	boolean save(T entity);

	T findById(long id);

	List<T> findAll();

	boolean deleteById(long id);

	List<T> findAllSorted(Comparator<? super T> order);
}
