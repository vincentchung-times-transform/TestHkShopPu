package com.hkshopu.hk.ui.main.store.adapter

interface ITHelperInterface {

    fun onItemMove(fromPosition:Int,toPosition:Int)   //處理用上下拖曳來產生換位效果
    fun onItemDissmiss(position:Int)                  //處理用左右滑動來刪除項目

}