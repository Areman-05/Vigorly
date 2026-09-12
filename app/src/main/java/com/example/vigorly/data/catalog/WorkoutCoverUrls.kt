package com.example.vigorly.data.catalog

import com.example.vigorly.data.model.WorkoutType
import kotlin.math.absoluteValue

/**
 * Portadas Unsplash temáticas. Claim global: **ninguna URL de hero/categoría se repite**.
 * Solo IDs verificados (HEAD 200).
 */
object WorkoutCoverUrls {

    private fun u(id: String): String =
        "https://images.unsplash.com/photo-$id?auto=format&fit=crop&w=1200&q=80"

    private fun keyOf(url: String): String = url.substringBefore('?')

    private val claimed = linkedSetOf<String>()
    private val workoutOverrides = mutableMapOf<String, String>()
    private val workoutAssigned = mutableMapOf<String, String>()
    private val categoryByType = mutableMapOf<WorkoutType, String>()
    private val tipCovers = mutableListOf<String>()
    private val keyPool = mutableListOf<String>()
    private val fallbackPool = mutableListOf<String>()

    private fun claim(url: String): String {
        val k = keyOf(url)
        check(claimed.add(k)) { "Portada duplicada: $k" }
        fallbackPool += url
        return url
    }

    private fun claimNext(pool: List<String>): String {
        for (url in pool) {
            if (keyOf(url) !in claimed) return claim(url)
        }
        error("Pool agotado sin portadas libres")
    }

    private val strengthPool = listOf(
        u("1534438327276-14e5300c3a48"),
        u("1581009146145-b5ef050c2e1e"),
        u("1526506118085-60ce8714f8c5"),
        u("1583454110551-21f2fa2afe61"),
        u("1517836357463-d25dfeac3438"),
        u("1571019613454-1cb2f99b2d8b"),
        u("1541534741688-6078c6bfb5c5"),
        u("1605296867304-46d5465a13f1"),
        u("1594381898411-846e7d193883"),
        u("1554344728-77cf90d9ed26"),
        u("1627483297886-49710ae1fc22"),
        u("1571902943202-507ec2618e8f"),
        u("1567013127542-490d757e51fc"),
        u("1556817411-31ae72fa3ea0"),
        u("1549576490-b0b4831ef60a"),
        u("1770664612843-b44e26070024"),
        u("1685633224976-cc0316d16dc1"),
        u("1685633225009-9a612a71b945"),
        u("1685633225086-a762f29f2d03")
    )

    private val hiitPool = listOf(
        u("1518611012118-696072aa579a"),
        u("1599058917212-d750089bc07e"),
        u("1599058917765-a780eda07a3e"),
        u("1434596922112-19c563067271"),
        u("1601422407692-ec4eeec1d9b3"),
        u("1594737625785-a6cbdabd333c"),
        u("1518310383802-640c2de311b2"),
        u("1607962837359-5e7e89f86776"),
        u("1550345332-09e3ac987658")
    )

    private val cardioPool = listOf(
        u("1476480862126-209bfaa8edc8"),
        u("1483721310020-03333e577078"),
        u("1552674605-db6ffd4facb5"),
        u("1571019614242-c5c5dee9f50b"),
        u("1475724017904-b712052c192a"),
        u("1493225457124-a3eb161ffa5f"),
        u("1558017487-06bf9f82613a"),
        u("1685633224860-7655234d9cd5")
    )

    private val recoveryPool = listOf(
        u("1544367567-0f2fcb009e0b"),
        u("1506126613408-eca07ce68773"),
        u("1549060279-7e168fcee0c2"),
        u("1575052814086-f385e2e2ad1b"),
        u("1599447421416-3414500d18a5"),
        u("1545205597-3d9d02c29597"),
        u("1591741535585-9c4f52b3f13f"),
        u("1646239646963-b0b9be56d6b5")
    )

    private val pilatesPool = listOf(
        u("1548690312-e3b507d8c110"),
        u("1574680178050-55c6a6a96e0a"),
        u("1676107240833-c5475c83c56a"),
        u("1758599880788-e49f6ee77bc7"),
        u("1758599879133-e1f34aeccf63"),
        u("1758599878798-6e6ecd854c6b")
    )

    private val mobilityPool = listOf(
        u("1545389336-cf090694435e"),
        u("1574680096145-d05b474e2155"),
        u("1767611118992-b300c4fb4bee"),
        u("1758599878905-212767677110"),
        u("1633707236776-1188a396f223"),
        u("1552196563-55cd4e45efb3"),
        u("1758274538676-87c2314bca60")
    )

    private val swimPool = listOf(
        u("1530549387789-4c1017266635"),
        u("1541689186060-3b08be2fd22f"),
        u("1519315901367-f34ff9154487"),
        u("1495157907198-a5b997c29047"),
        u("1576013551627-0cc20b96c2a7"),
        u("1560089000-7433a4ebbd64"),
        u("1602019248763-e75f48b55a2a"),
        u("1619334910286-c613d0d1a4d1")
    )

    private val tipPool = listOf(
        u("1758599881247-b7a28d9297e5"),
        u("1637157216470-d92cd2edb2e8"),
        u("1641913640860-ab4c2bfb2bb0"),
        u("1659303388062-d61383748059")
    )

    private val extrasPool = listOf(
        u("1660847427733-4986774f5f87"),
        u("1566920226683-7d2d19a0d0f7"),
        u("1649830762484-451be286eb02"),
        u("1620680160479-4a460c144955"),
        u("1711637819287-2ebcd2c45b42"),
        u("1529156446057-63af7e5c1337"),
        u("1685633224973-47258dc0e47e"),
        u("1685633225160-c98468c4b6f6"),
        u("1685633225198-41691f35cc4c"),
        u("1685633225377-50d1f1e01fee"),
        u("1685633224834-ed00df895aa1"),
        u("1685633225183-441289454a8d"),
        u("1685633225603-9a1ffafd11fe"),
        u("1685633225160-9b965f1715ff"),
        u("1685633224966-fd18820f6016"),
        u("1685633224893-59e8c14f6ad2")
    )

    private fun poolFor(type: WorkoutType): List<String> = when (type) {
        WorkoutType.STRENGTH -> strengthPool
        WorkoutType.HIIT -> hiitPool
        WorkoutType.CARDIO -> cardioPool
        WorkoutType.RECOVERY -> recoveryPool
        WorkoutType.PILATES -> pilatesPool
        WorkoutType.MOBILITY -> mobilityPool
        WorkoutType.SWIM -> swimPool
    }

    init {
        workoutOverrides["aqua_aerobic"] = claim(u("1541689186060-3b08be2fd22f"))
        workoutOverrides["swim_technique"] = claim(u("1519315901367-f34ff9154487"))
        workoutOverrides["morning_swim"] = claim(u("1495157907198-a5b997c29047"))
        workoutOverrides["pilates_core_flow"] = claim(u("1574680178050-55c6a6a96e0a"))
        workoutOverrides["pilates_core"] = claim(u("1676107240833-c5475c83c56a"))
        workoutOverrides["foam_rolling"] = claim(u("1591741535585-9c4f52b3f13f"))
        workoutOverrides["stretch_cooldown"] = claim(u("1545205597-3d9d02c29597"))

        categoryByType[WorkoutType.STRENGTH] = claimNext(strengthPool)
        categoryByType[WorkoutType.HIIT] = claimNext(hiitPool)
        categoryByType[WorkoutType.CARDIO] = claimNext(cardioPool)
        categoryByType[WorkoutType.RECOVERY] = claimNext(recoveryPool)
        categoryByType[WorkoutType.PILATES] = claimNext(pilatesPool)
        categoryByType[WorkoutType.MOBILITY] = claimNext(mobilityPool)
        categoryByType[WorkoutType.SWIM] = claimNext(swimPool)

        tipPool.forEach { tipCovers += claim(it) }

        extrasPool.forEach { url ->
            if (keyOf(url) !in claimed) keyPool += url
        }
        listOf(strengthPool, hiitPool, cardioPool, recoveryPool, pilatesPool, mobilityPool, swimPool)
            .flatten()
            .forEach { url ->
                if (keyOf(url) !in claimed && url !in keyPool) keyPool += url
            }
    }

    fun category(type: WorkoutType): String = categoryByType.getValue(type)

    fun forWorkout(id: String, type: WorkoutType): String {
        workoutAssigned[id]?.let { return it }
        workoutOverrides[id]?.let { url ->
            workoutAssigned[id] = url
            return url
        }
        val url = claimNext(poolFor(type))
        workoutAssigned[id] = url
        return url
    }

    /** @deprecated Prefer [forWorkout]; se mantiene por compat. */
    fun forType(type: WorkoutType, index: Int): String =
        forWorkout("__legacy_${type}_$index", type)

    fun tip(index: Int): String {
        if (tipCovers.isEmpty()) return fallback
        val i = index.mod(tipCovers.size).let { if (it < 0) it + tipCovers.size else it }
        return tipCovers[i]
    }

    fun forKey(key: String): String {
        val pool = if (keyPool.isNotEmpty()) keyPool else fallbackPool
        if (pool.isEmpty()) return fallback
        var hash = 0
        for (ch in key) hash = 31 * hash + ch.code
        return pool[hash.absoluteValue.mod(pool.size)]
    }

    val fallback: String
        get() = fallbackPool.firstOrNull() ?: u("1534438327276-14e5300c3a48")

    fun fallbackFor(failedUrl: String?): String {
        if (fallbackPool.isEmpty()) return fallback
        val seed = failedUrl ?: "x"
        var hash = 0
        for (ch in seed) hash = 31 * hash + ch.code
        return fallbackPool[hash.absoluteValue.mod(fallbackPool.size)]
    }
}
