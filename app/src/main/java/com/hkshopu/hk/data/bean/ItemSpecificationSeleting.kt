package com.HKSHOPU.hk.data.bean


data class ItemSpecificationSeleting(
    var spec_id: String, var spec_name: String, var price_range: String, var quant_range: String, var total_quantity: Int, var seleted_status : Boolean = false,) {
}