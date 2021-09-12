package com.jayesh.dindinntest.orderModels

data class Orders(var id: Int, var items:List<Items>, var created_at:String, var alerted_at:String, var expired_at:String)