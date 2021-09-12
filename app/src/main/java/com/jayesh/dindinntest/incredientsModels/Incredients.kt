package com.jayesh.dindinntest.incredientsModels

data class Incredients(var id: Int,var category_id: Int,var title: String, var items:List<IncredientsMenu>, var created_at:String)