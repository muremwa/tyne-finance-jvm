package com.tyne.finance.core.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "auth_group")
@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer groupID;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_group_permissions",
            joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")}
    )
    private List<Permission> permissions;
}
