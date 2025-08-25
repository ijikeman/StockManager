package com.example.stock.repository

import com.example.stock.model.Owner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OwnerRepository : JpaRepository<Owner, Int> {
    fun findByName(name: String): Owner?
}
