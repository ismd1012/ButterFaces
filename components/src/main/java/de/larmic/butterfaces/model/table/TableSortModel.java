package de.larmic.butterfaces.model.table;

/**
 * Created by larmic on 03.12.14.
 */
public interface TableSortModel {

    /**
     * @param columnId optional column id (generated id will be used is column id attribute is not set)
     * @param sortBy   optional column component attribute
     * @param sortType the {@link de.larmic.butterfaces.model.table.SortType}
     */
    void sortColumn(final String columnId, final String sortBy, final SortType sortType);

    SortType getSortType(final String clientId);
}