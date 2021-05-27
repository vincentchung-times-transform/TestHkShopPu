package com.hkshopu.hk.component

import androidx.fragment.app.Fragment
import com.hkshopu.hk.data.bean.ProductCategoryBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.data.bean.ShopCategoryBean

/**
 * Created by Administrator on 2018/4/20 0020.
 */

//Login Events
class EventLoginSuccess
class EventLogout

//Add Shop Events
class EventShopNameUpdated(val shopName: String? = null)
class EventShopDesUpdated(val shopDes: String? = null)
class EventShopCatSelected(val list: ArrayList<ShopCategoryBean>)

class EventChangeShopCategory(val list: ArrayList<ShopCategoryBean>)
class EventAddShopSuccess()
class EventGetShopCatSuccess(val list: ArrayList<String>)
class EventAddShopBriefSuccess(val description: String?)
class EventChangeShopPhoneSuccess(val phone: String?)
class EventChangeShopEmailSuccess(val email: String?)
class EventChangeShopTitleSuccess(val shopname: String?)
class EventGetBankAccountSuccess(val list: ArrayList<ShopBankAccountBean>)
//Add Product Events

class EventProductCatSelected(val selectrdId: Int = 1, var c_product_category: String)
class EventProductCatLastPostion(val postion: Int = 1)



class EventCheckShipmentEnableBtnOrNot(val boolean : Boolean)

class EventCheckFirstSpecEnableBtnOrNot(val boolean : Boolean)
class EventCheckSecondSpecEnableBtnOrNot(val boolean : Boolean)
class EventCheckInvenSpecEnableBtnOrNot(val boolean : Boolean)
class EventInvenSpecDatasRebuild(val boolean : Boolean)


class EventTransferToFragmentAfterUpdate(val index : Int)

//Other Events (Not Used)

//Add Product Events
class EventProductSearch(val keyword: String = "")
class EventProductDelete(val boolean: Boolean)
class EventdeleverFragmentAfterUpdateStatus(val action : String)
class EventRefreshShopInfo

//Other Events (Not Used)

class EventPhoneShow(val show:Boolean,val phone: String? = null)


class EventLaunchConfigsSuccess

class EventRechargeSuccess

class EventRefreshShopList
class EventRefreshAddressList

class EventRefreshHome

class EventReadHistoryUpdated(val bookId: Int? = null)

class EventReadToHome

class EventHideBottomBar

class EventShowBottomBar

class EventReturnComic

class EventEmailShow(val show:Boolean,val email: String? = null)

class EventSyncBank

class EventAutoSwitch
