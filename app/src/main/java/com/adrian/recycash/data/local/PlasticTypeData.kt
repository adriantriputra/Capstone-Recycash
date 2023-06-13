package com.adrian.recycash.data.local

import android.content.Context
import com.adrian.recycash.R
import com.adrian.recycash.data.remote.response.PlasticType

object PlasticTypeData {
    fun getPlasticTypes(context: Context): List<PlasticType> {
        return listOf(
            PlasticType(
                context.getString(R.string.plastic_type_1),
                context.getString(R.string.plastic_type_1_description),
                R.drawable.symbol_resin_code_1_pete
            ),
            PlasticType(
                context.getString(R.string.plastic_type_2),
                context.getString(R.string.plastic_type_2_description),
                R.drawable.symbol_resin_code_2_hdpe
            ),
            PlasticType(
                context.getString(R.string.plastic_type_3),
                context.getString(R.string.plastic_type_3_description),
                R.drawable.symbol_resin_code_3_v
            ),
            PlasticType(
                context.getString(R.string.plastic_type_4),
                context.getString(R.string.plastic_type_4_description),
                R.drawable.symbol_resin_code_4_ldpe
            ),
            PlasticType(
                context.getString(R.string.plastic_type_5),
                context.getString(R.string.plastic_type_5_description),
                R.drawable.symbol_resin_code_5_pp
            ),
            PlasticType(
                context.getString(R.string.plastic_type_6),
                context.getString(R.string.plastic_type_6_description),
                R.drawable.symbol_resin_code_6_ps
            ),
            PlasticType(
                context.getString(R.string.plastic_type_7),
                context.getString(R.string.plastic_type_7_description),
                R.drawable.symbol_resin_code_7_other
            ),
        )
    }
    
    fun options(context: Context): List<String> {
        return listOf(
            context.getString(R.string.type_1),
            context.getString(R.string.type_2),
            context.getString(R.string.type_3),
            context.getString(R.string.type_4),
            context.getString(R.string.type_5),
            context.getString(R.string.type_6),
            context.getString(R.string.type_7)
        )
    }
}