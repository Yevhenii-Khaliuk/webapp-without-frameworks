package com.khaliuk.dao;

class EntityTableMapper {
    private enum TablesOfEntities {
        USERS("User"), ROLES("Roles"),
        CATEGORIES("Category"), PRODUCTS("Product");

        private String entity;

        TablesOfEntities(String entity) {
            this.entity = entity;
        }

        private String getEntity() {
            return entity;
        }
    }

    static String getTable(String entity) {
        return TablesOfEntities.valueOf(entity).toString();
    }
}
