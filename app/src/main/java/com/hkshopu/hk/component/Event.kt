package com.HKSHOPU.hk.component

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.HKSHOPU.hk.data.bean.DetailedProductSpecificationBean
import com.HKSHOPU.hk.data.bean.ItemSpecificationSeleting
import com.HKSHOPU.hk.data.bean.ShopBankAccountBean
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.facebook.internal.Mutable

/**
 * Created by Administrator on 2018/4/20 0020.
 */

//Login Events
class EventLoginSuccess
class EventLogout

//shoplistFragment
class EventRefreshShopList()

//Add Shop Events
class EventShopNameUpdated(val shopName: String? = null)
class EventShopDesUpdated(val shopDes: String? = null)
class EventShopCatSelected(val list: ArrayList<ShopCategoryBean>)
class EventChangeShopCategory(val list: ArrayList<ShopCategoryBean>)
class EventAddShopSuccess()
class EventGetShopCatSuccess(val list: ArrayList<String>)

//ShopInfoFragment
class EventRefreshShopInfo()
class EventRefreshShopFucition()

//ShopmenuActivity
class EventStartLoadingShopmenu()
class EventFinishLoadingShopmenu()

//MyStoreFragment
class EventAddShopBriefSuccess(val description: String)
class EventMyStoreFragmentRefresh()
class EventAddProductButtonVisibility(var boolean: Boolean)

//ShopInfoModifying
class EventChangeShopPhoneSuccess(val phone: String?)
class EventChangeShopEmailSuccess(val email: String?)
class EventChangeShopTitleSuccess(val shopname: String?)
class EventGetBankAccountSuccess(val list: ArrayList<ShopBankAccountBean>)

//Add Product Events
//Category Selecting
class EventProductCatSelected(val selectdId: String = "", var c_product_category: String)
class EventProductCatLastPostion(val postion: Int = 1)
//Shipment Setting
class EventCheckShipmentEnableBtnOrNot(val boolean : Boolean)
//Logistics Setting
class EventCheckLogisticsEnableBtnOrNot(val boolean : Boolean)
//Specification Setting
class EventCheckFirstSpecEnableBtnOrNot(val boolean : Boolean)
class EventCheckSecondSpecEnableBtnOrNot(val boolean : Boolean)
class EventCheckInvenSpecEnableBtnOrNot(val boolean : Boolean)
class EventInvenSpecDatasRebuild(val boolean : Boolean)

//MyProductsActivity
class EventTransferToFragmentAfterUpdate(val index : Int)
class EventLoadingStatus(val boolean: Boolean)
class EventProductSearch(val keyword: String = "")
class EventProductDelete(val boolean: Boolean)
class EventdeleverFragmentAfterUpdateStatus()


//Detailed Products Info For Buyer
class EventBuyerDetailedProductBottomSheetShowHide(
    var mode:String,
    var product_id:String,
    var product_name:String,
    var stock_up_days:Int,
    var other_detailed_product_specification_bean: DetailedProductSpecificationBean
)
class EventBuyerDetailedProductBottomSheetConfirmToOtherProduct(
    var spec_spinner_content_value:String,
    var price_range: String,
    var spec_id: String
)

class EventBuyerDetailedProductBtnStatusFirst(
    val boolean: Boolean,
    val position: Int,
    var spec_id: String,
    var spec_name: String,
    var price_range: String,
    var quant_range: String,
    var total_quant: Int)
class EventBuyerDetailedProductBtnStatusSecond(
    val boolean: Boolean,
    val position: Int,
    var spec_id: String,
    var spec_name: String,
    var price_range: String,
    var quant_range: String,
    var total_quant: Int)

class EventBuyerDetailedProductNewProDetailedFragment(var id : String)
class EventBuyerDetailedProductRemoveProDetailedFragment(var fragment: Fragment)

//Shopping Cart

class EventRemoveShoppingCartItem(var shop_id: String, var item_id_list_json: String, var position: Int)
class EventUpdateShoppingCartItem(var shopping_cart_item_id: String, var new_quantity : String, var selected_shipment_id : String, var selected_user_address_id: String, var selected_payment_id : String)
class EventUpdateShoppingCartItemForConfirmed(var id : String, var buyerName: String, var buyerPhone: String, var buyerAddress: String, var shoppingCartShopId: String, var specId_json:String, var address_updateMode:String)
class EventCheckedShoppingCartItem()
class EventRefreshShoppingCartItemCount()
class EventRefreshShoppingCartBuyerAddressList()

//RefreshUser
class EventRefreshUserInfo()
class EventRefreshUserAddressList

//Search
class EventToUserProfile()
class EventToShopSearch()
class EventToProductSearch()

//ProductCatSelectedToSearch
class EventProductCatSelectedToSearch(val selectrdId: String = "", var c_product_category_selected: String)

//ShopPreView
class EventShopPreViewRankAll(val shopId: String = "")

//Shopmenu
class EventShopmenuToSpecificPage(var index: Int = 0)

//PurchaseList
class EventPurchaseListToSpecificPage(var index: Int = 0)
//SaleList
class EventSaleListToSpecificPage(var index: Int = 0)

//Order
class EventGenerateOeder()

//Other Events (Not Used)

class EventPhoneShow(val show:Boolean,val phone: String? = null)

class EventLaunchConfigsSuccess

class EventRechargeSuccess

class EventRefreshAddressList

class EventRefreshHome

class EventReadHistoryUpdated(val bookId: String? = null)

class EventReadToHome

class EventHideBottomBar

class EventShowBottomBar

class EventReturnComic

class EventEmailShow(val show:Boolean,val email: String? = null)

class EventSyncBank

class EventAutoSwitch

//fps
class EventRefreshFpsAccountList
