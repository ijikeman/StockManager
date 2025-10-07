package com.example.stock.service

import com.example.stock.model.Sector
import com.example.stock.repository.SectorRepository
import org.springframework.stereotype.Service

@Service
open class SectorService(
    private val sectorRepository: SectorRepository
) {
    open fun findAll(): List<Sector> {
        return sectorRepository.findAll()
    }

    open fun findByName(name: String): Sector? {
        return sectorRepository.findByName(name)
    }

    /**
     * IDに基づいてセクターを検索します。
     * @param id 検索するセクターのID
     * @return 見つかったセクター。存在しない場合はnull。
     */
    open fun findById(id: Int): Sector? {
        return sectorRepository.findById(id).orElse(null)
    }

    // 追加する
    open fun save(sector: Sector): Sector {
        return sectorRepository.save(sector)
    }

    // 削除する
    open fun deleteById(id: Int) {
        sectorRepository.deleteById(id)
    }
}
