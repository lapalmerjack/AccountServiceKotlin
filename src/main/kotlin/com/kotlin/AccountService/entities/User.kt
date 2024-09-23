package com.kotlin.AccountService.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,

    @NotBlank
    @NotNull
    var name: String,

    @NotBlank
    @NotNull
    var lastname: String,

    @NotBlank
    @NotNull
    @Email(regexp = ".+@acme.com$")
    var email: String,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @field:Size(min = 12, message = "Password length must be 12 chars minimum!")
    var password: String,

    @Column(name = "failed_attempts")
    @JsonIgnore
    var loginAttempts: Int = 0,

    @JsonIgnore
    var isAccountUnLocked: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    @JsonIgnore
    var salaries: MutableList<Salary> = mutableListOf(),

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()

) {

    constructor(name: String, lastname: String, email: String, password: String) :
            this(null, name, lastname, email, password)

    fun addPayment(payment: Salary) {
        salaries.add(payment)
        payment.employee = email
    }


    fun addRole(role: Role) {
        roles.add(role)
    }
}
