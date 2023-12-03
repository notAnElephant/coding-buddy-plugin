package com.github.notanelephant.codingbuddyplugin.wrapper.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Permission(
    /**
     * id associated with the model
     */
    val id: String,
    /**
     * Object type of permission
     */
    val `object`: String,
    /**
     * Creation time in epoch seconds
     */
    val created: Long,
    /**
     * Whether engine is allowed to be created
     */
    @SerialName("allow_create_engine")
    val allowCreateEngine: Boolean,
    /**
     * Sampling allowed/not allowed
     */
    @SerialName("allow_sampling")
    val allowSampling: Boolean,
    /**
     * Whether logprobs is allowed
     */
    @SerialName("allow_logprobs")
    val allowLogprobs: Boolean,
    /**
     * Whether search indices is allowed
     */
    @SerialName("allow_search_indices")
    val allowSearchIndices: Boolean,
    /**
     * Whether view is allowed
     */
    @SerialName("allow_view")
    val allowView: Boolean,
    /**
     * Whether fine-tuning is allowed
     */
    @SerialName("allow_fine_tuning")
    val allowFineTuning: Boolean,
    /**
     * Organization of the model
     */
    val organization: String,
    /**
     * Group to the model
     */
    val group: String? = null,
    /**
     * Whether model is blocking
     */
    @SerialName("is_blocking")
    val isBlocking: Boolean,
)
