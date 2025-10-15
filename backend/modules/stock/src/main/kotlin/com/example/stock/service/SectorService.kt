package com.example.stock.service

import com.example.stock.model.Sector
import com.example.stock.repository.SectorRepository
import org.springframework.stereotype.Service

@Service
class SectorService(
    private val sectorRepository: SectorRepository
) {
    fun findAll(): List<Sector> {
        return sectorRepository.findAll()
    }

    fun findByName(name: String): Sector? {
        return sectorRepository.findByName(name)
    }

    /**
     * IDに基づいてセクターを検索します。
     * @param id 検索するセクターのID
     * @return 見つかったセクター。存在しない場合はnull。
     */
    fun findById(id: Int): Sector? {
        return sectorRepository.findById(id).orElse(null)
    }

    // 追加する
    fun save(sector: Sector): Sector {
        return sectorRepository.save(sector)
    }

    // 削除する
    fun deleteById(id: Int) {
        sectorRepository.deleteById(id)
    }
}
