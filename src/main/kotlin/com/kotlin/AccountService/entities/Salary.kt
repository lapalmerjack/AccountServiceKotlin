package com.kotlin.AccountService.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Pattern

@Entity
@Table(name = "payments")
data class Salary(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "salary_id")
    var id: Long? = null,

    @Column(name = "employee", nullable = false)
    var employee: String,

    @Column(name = "period", nullable = false)
    @Pattern(regexp = "^(0[1-9]|1[0-2])-(20\\d{2})$", message = "Invalid date")
    var period: String,

    @Column(name = "salary", nullable = false)
    var salary: Long,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST])
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: User

)
