package com.kotlin.AccountService.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*


@Entity
@Table(name = "role")
data class Role(
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private val id: Long? = null,

    @field:Column(name = "role") var userRole: String)