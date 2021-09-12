package com.jayesh.dindinntest.orderModels

import com.jayesh.dindinntest.models.StatusWrapper

data class OrderWrapper(var status: StatusWrapper, var data:List<Orders>)