package com.hkshopu.hk.component

import com.hkshopu.hk.data.bean.ProductCategoryBean

/**
 * Created by Administrator on 2018/4/20 0020.
 */
class EventLoginSuccess

class EventLogout

class EventShopNameUpdated(val shopName: String? = null)

class EventShopDesUpdated(val shopDes: String? = null)

class EventShopCatSelected

class EventProductCatSelected(val selectrdId: Int = 1)

class EventProductCatLastPostion(val postion: Int = 1)

class EventLaunchConfigsSuccess

class EventRechargeSuccess

class EventRefreshHome

class EventReadHistoryUpdated(val bookId: Int? = null)

class EventReadToHome

class EventHideBottomBar

class EventShowBottomBar

class EventReturnComic

class EventToMine

class EventToRecharge

class EventToBulletin

class EventAutoSwitch
