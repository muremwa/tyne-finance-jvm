package com.tyne.finance.core.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Table(name = "core_user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger userID;

    @ToString.Exclude
    @Column(name = "password")
    private String dbPassword;

    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "date_joined")
    private Timestamp dateJoined;

    @ToString.Exclude
    @Transient
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    private Currency currency;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "core_user_groups",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")}
    )
    private List<Group> groups;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "core_user_user_permissions",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")}
    )
    private List<Permission> permissions;

    public List<Permission> getAllPermissions() {
        Set<Permission> permissionSet = new HashSet<>(this.permissions);
        for (Group group: this.getGroups()) {
            permissionSet.addAll(group.getPermissions());
        }
        return permissionSet.stream().toList();
    }
}
