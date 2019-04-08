package com.khaliuk.model;

import com.khaliuk.annotations.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "ROLES")
public class Role {
    private Long id;
    private RoleName roleName;
    private List<User> users = new ArrayList<>();

    public Role() {
    }

    public Role(Long id, RoleName roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public enum RoleName {
        USER, MODERATOR, ADMIN
    }
}
