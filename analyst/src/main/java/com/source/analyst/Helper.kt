package com.source.analyst

import kotlinx.coroutines.CancellationException

object Helper {
    fun getUrl(
        firstPartOfUrl: String,
        campaign: String?,
        isFromFacebook: Boolean,
        gId: String?
    ): String {
        val subList = getSubs(campaign)

        val mediaSource =
            if (campaign.isNullOrBlank()) ""
            else
                if (isFromFacebook) "${Filter.deFilter("rn")}_${Filter.deFilter("mpe")}"
                else Filter.deFilter("dqrqddqd")

        val resultBuilder = StringBuilder()

        resultBuilder.apply {
            append("$firstPartOfUrl?")

            subList?.mapIndexed { index, subValue ->
                append("${Filter.deFilter("egn")}${index + 1}=$subValue&")
            } ?: append("${Filter.deFilter("egn")}1=${Filter.deFilter("ads")}&")

            append("${Filter.deFilter("ogefayqd")}_${Filter.deFilter("up")}=$gId&")
            append("${Filter.deFilter("bxmfrady")}=$mediaSource&")
        }

        return resultBuilder.toString()
    }

    private fun getSubs(campaign: String?): List<String>? {
        val subPart = try {
            campaign?.split("//")?.get(1)
        } catch (e: Exception) {
            if (e is CancellationException) throw CancellationException()

            campaign
        }

        return try {
            subPart?.split("_")
        } catch (e: Exception) {
            if (e is CancellationException) throw CancellationException()

            subPart?.let { listOf(it) }
        }
    }
}