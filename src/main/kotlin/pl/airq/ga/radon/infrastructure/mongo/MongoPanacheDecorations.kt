package pl.airq.ga.radon.infrastructure.mongo

import io.quarkus.mongodb.panache.common.runtime.CommonPanacheQueryImpl
import io.quarkus.mongodb.panache.kotlin.PanacheQuery
import io.quarkus.mongodb.panache.kotlin.runtime.PanacheQueryImpl
import org.apache.commons.lang3.reflect.FieldUtils
import org.apache.commons.lang3.reflect.MethodUtils

private val panacheQueryImplDelegateField = FieldUtils.getField(PanacheQueryImpl::class.java, "delegate", true)
private val commonPanacheQueryImplListMethod =
    MethodUtils.getMatchingMethod(CommonPanacheQueryImpl::class.java, "list", Integer::class.java)
        .also { it.isAccessible = true }

fun <T : Any> PanacheQuery<T>.listWithLimit(limit: Int?): List<T> {
    if (limit == null) return list()
    if (this !is PanacheQueryImpl<T>) return list()

    run {
        val commonPanacheQueryImpl = panacheQueryImplDelegateField.get(this) as CommonPanacheQueryImpl<T>
        return commonPanacheQueryImplListMethod.invoke(commonPanacheQueryImpl, limit) as List<T>
    }
}

