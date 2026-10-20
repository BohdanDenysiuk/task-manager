package com.bohdandenysiuk.taskmanager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class InMemoryRepository<T> implements Repository<T> {
	private final List<T> entities = new ArrayList<>();

	protected abstract Long getEntityId(T entity);

	@Override
	public boolean save(T entity) {
		if (entity == null) {
			return false;
		}

		Long id = this.getEntityId(entity);
		if (id == null || findById(id) != null) {
			return false;
		}

		entities.add(entity);
		return true;
	}

	@Override
	public T findById(long id) {
		for (T entity : entities) {
			Long value = getEntityId(entity);

			if (value != null && value == id) {
				return entity;
			}
		}

		return null;
	}

	@Override
	public List<T> findAll() {
		return new ArrayList<>(entities);
	}

	@Override
	public boolean deleteById(long id) {
		Iterator<T> iterator = entities.iterator();

		while (iterator.hasNext()) {
			Long elementId = getEntityId(iterator.next());
			if (elementId != null && elementId == id) {
				iterator.remove();
				return true;
			}
		}

		return false;
	}

	@Override
	public List<T> findAllSorted(Comparator<? super T> order) {
		if (order == null) {
			throw new IllegalArgumentException("Sort order must not be null");
		}

		List<T> copy = findAll();
		copy.sort(order);

		return copy;
	}
}
