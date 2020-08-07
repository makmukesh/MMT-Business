package com.vpipl.mmtbusiness.Utils;

import android.content.Context;

import com.vpipl.mmtbusiness.AppController;

/**
 * Created by admin on 01-05-2017.
 */
public class QueryUtils {

    public static String methodToGetProductDetail = "Item_Details";
    public static String methodToGetCheckOutDeliveryAddress = "CheckOutDeliveryAddress";
    public static String methodToGetSearchProductsList = "SearchProductListKeyworks";
    public static String methodToAddCheckOutDeliveryAddress = "CheckOutAddNewAddress";
    public static String methodToCheckOutEditAddress = "CheckOutEditAddress";
    public static String methodToCheckOutDeleteAddress = "CheckOutDeleteAddress";
    public static String methodToGetViewOrdersList = "MyOrders";
    public static String methodToGetViewOrdersDetails = "MyOrderDetils";
    public static String methodAppAllCategory = "AppMenuCategory_AllCategory";
    public static String methodAppMenuCategory_AllCategoryNew = "AppMenuCategory_AllCategoryNew";
    public static String methodToGetProductList = "ProductView_ProductCount";
    public static String methodToAddOrder = "AddOrder";
    public static String methodToAddOrderforWallet = "AddOrderforWallet";
    public static String methodToThanksPage = "ThanksPage";
    public static String methodToSendSMSForOrder = "SendSMSForOrder";
    public static String methodToSendMailForOrder = "SendMailForOrder";
    public static String methodMemberLoginOnPortal = "MemberLoginOnPortal";
    public static String methodTransactionLogin = "TransactionLogin";
    public static String methodtoNewJoining = "NewJoining";
    public static String methodtoSendOTP = "SendJoiningOTP";
    public static String methodToChangePassword = "ChangePassword";
    public static String methodToGetUserProfile = "ViewProfile";
    public static String methodToUpdateUserProfile = "UpdateProfile";
    public static String methodMaster_FillState = "Master_FillState";
    public static String methodSponsorPageFillPackage = "SponsorPageFillPackage";
    public static String methodSponsorActivationWeek = "SponsorActivationWeek";
    public static String methodMaster_FillBank = "Master_FillBank";
    public static String methodGet_BankDetail = "Get_BankDetail";
    public static String methodUploadImages = "UploadKYCImages";
    public static String methodGetImages = "ReadKYCImages";
    public static String methodWelcomeLetter = "WelcomeLetter";
    public static String methodToGetSponsorTeamDetail = "SponsorTeamDetail";
    public static String methodToGetMyDirectMembers = "MyDirectMembers";
    public static String methodToGetDownlineTeamDetail = "DownlineTeamDetail";
    public static String methodToGetWeeklyIncentiveDetail = "WeeklyIncentiveDetail";
    public static String methodToGetMonthlyIncentive = "MonthlyIncentive";
    public static String methodToGetMonthlyIncentiveDetailReport = "MonthlyIncentiveDetailReport";
    public static String methodToGetSingleLegBonus = "SingleLegBonus";
    public static String methodToGetDashboardDetail = "Dashboard";
    public static String methodToGetDashboard_New_API = "Dashboard_New_API";
    public static String methodToForgetPasswordMember = "ForgotPassword";
    public static String methodToApplicationStatus = "ApplictionStatus";
    public static String methodtoGenerated_IssuedPinDetails = "Generated_IssuedPinDetails";
    public static String methodToGenerated_IssuedPinDetails_BillDetails = "Generated_IssuedPinDetails_BillDetails";
    public static String methodToEPinDetail = "EPinDetail";
    public static String methodCheckTopupIDNo = "CheckTopupIDNo";
    public static String methodTopupMember = "TopupMember";
    public static String CheckEPinTransferMember = "CheckEPinTransferMember";
    public static String EPinTransfer = "EPinTransfer";
    public static String EPinTransferLoadPackage = "EPinTransferLoadPackage";
    public static String PinTransferReport = "PinTransferReport";
    public static String PinReceiveReport = "PinReceiveReport";
    public static String PinTransferDetails = "PinTransferDetails";
    public static String PinReceiveReportDetails = "PinReceiveReportDetails";
    public static String methodtoLoadBillType = "LoadBillType";
    public static String methodtoWeeklyStatement = "WeeklyIncentiveStatement";
    public static String methodtoGetDrawerMenuItems = "DashBoardMenu";
    public static String methodToGetVersion = "CheckVersionInfo";
    public static String methodToGetWallet_Request_Status_Report = "WalletRequestReport";
   // public static String methodToRequestEpin = "RequestEPin";
    public static String methodToRequestEpin = "RequestEPinNew";
   // public static String methodToRequestEpin_Cash = "RequestEPin";
    public static String methodToRequestEpin_Cash = "RequestEPinNew";
    public static String methodToGetEpinRequestReport = "PinRequestDetailsDateWise";
    public static String methodToPinRequestPackage = "PinRequestPackage";
    public static String methodToROIPlan = "ROIReport_PlanType";
    public static String methodToROIReport_KitMaster = "ROIReport_KitMaster";
    public static String methodLoad_ROIDetail = "ROIDetailReport";
    public static String methodROISummary = "ROISummary";
    public static String methodEnquiryandComplaint = "EnquiryandComplaint";
    public static String methodHotSellingProducts = "HotSellingProducts";
    public static String methodToGetServiceProviderList = "OperatorList";
    public static String methodToDoRecharge = "DoRecharge";
    public static String methodToGet_CurrentSession = "Get_CurrentSession";
    public static String methodToGet_RechargeDetails = "RechargeDetails";
    public static String methodToGet_IDInvestmentDetail = "IDInvestmentDetail";
    public static String methodToGet_PeriodicalReport = "PeriodicalReport";
    public static String methodToGet_MonthSession = "MonthSession";
    public static String methodToGet_LevelCommission = "LevelCommission";
    public static String methodToGet_LevelCommissionDetailReport = "LevelCommissionDetailReport";
    public static String methodToGet_PaymentHistory = "PaymentHistory";
    public static String methodEncryptData = "EncryptData";
    public static String methodToGetWalletTransactionReport = "WalletTransactionDetail";

    /*     New Api with realcom */
    public static String methodToGetDashboard2 = "Dashboard2";
    public static String methodToWebEncryption = "EncryptData";
    /*New API Add on by mukesh 22-10-2018 17.07PM*/
    public static String methodToWalletAmountWithdrawRequest = "WalletAmountWithdrawRequest_New";
    public static String methodToWalletWithdrawalRequestReport = "WalletWithdrawalRequestReport";
    public static String methodToShoppingWalletTransactionDetail = "ShoppingWalletTransactionDetail";
    //public static String methodToGetAvailableShoppingWalletBalance = "GetAvailableWalletBalance";
    public static String methodToGetAvailableShoppingWalletBalance = "GetAvailableShoppingWalletBalance";
    public static String methodToGetAvailablePayoutWalletBalance = "GetAvailableWalletBalance";
    public static String methodToGetRewardsDetail = "RewardsDetail";
    public static String methodCheckSponsor = "CheckSponsor";
    public static String methodPayoutWalletTransactionDetail = "WalletTransactionDetail";
    public static String methodToDailyIncentiveDetail = "DailyIncentiveDetail";
    public static String methodToRequestWalletAmount = "RequestWalletAmount";
    public static String methodToGetWalletTransferReport = "WalletTransferReport";
    public static String methodToGetAvailableWalletandCheckDownlineMember = "GetAvailableWalletandCheckDownlineMember";
    public static String methodToRequestTransferWalletAmount = "WalletTransfer";
    public static String methodToSendOTPUpdateProfile = "SendOTPOnUpdateProfile";
    public static String methodToSendOTPOnChangeMobileNo = "SendOTPOnChangeMobileNo";
    public static String methodToChangeMobileNo = "ChangeMobileNo";
    public static String methodToLaunchingBonanza = "LaunchingBonanza";
    public static String methodSendMailForEnquiry = "SendMailForEnquiry";
    public static String methodLoadProductBonus = "LoadProductBonus";
    public static String methodSellProduct  = "SellProduct";
    public static String methodBuyProduct = "BuyProduct";
    /*New API Add on by mukesh 22-10-2018 17.07PM*/
    public static String methodRewardTransactionRequest = "RewardTransactionRequest";
    public static String methodWeeklySession = "WeeklySession";
    public static String methodRoyaltyIncentiveDetailReport = "RoyaltyIncentiveDetailReport";

/*Api added by mukesh on 05-08-2019 04:13 PM*/
    public static String methodDashboard_New_API1 = "Dashboard_New_API1";
    public static String methodDailySinglePoolIncentive = "DailySinglePoolIncentive";

    public static String methodDailyPoolDetails = "DailyPoolDetails";

    public static String methodHomePageSlider = "HomePageSlider";
    /*Api added by mukesh on 19-08-2019 12:22 PM*/
    public static String methodToCheckAppRunningStatus = "CheckAppRunningStatus";

    public static String methodToAddPurchaseBill = "AddPurchaseBill";
    public static String methodToFranchiseeList = "FranchiseeList";
    public static String methodPurchaseBillDetailForMember = "PurchaseBillDetailForMember";
    public static String methodCheckFranchiseeCode = "CheckFranchiseeCode";
    public static String methodSendOTPApprovePruchaseBill = "SendOTPApprovePruchaseBill";

    public static String methodSponsorVendorBVReport = "SponsorVendorBVReport";

    public static String getViewgenealogyURL(Context con) {
        String url = "";
        try {
            url = AppUtils.ViewgenealogyURL() + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getViewBinarygenealogyURL(Context con) {
        String url = "";
        try {
            url = AppUtils.ViewBinarygenealogyURL() + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String methodUserLoginOnPortal = "UserLoginOnPortal";
    public static String methodMaster_FillColor_Size = "GetColorandSize";
    public static String methodToForgetPasswordUser = "UserForgotPassword";
    public static String methodtoGuestUserReg = "GuestUserReg";
    public static String methodtoGuestUserReg_OneTime = "GuestUserReg_OneTime";
    public static String methodHomePageSectionjustdowntoSlider = "LoadBanner";
    public static String methodToGetProductSizeList = "FillItem_Size";
    public static String methodToGetProductColorList = "FillItem_Color";
    public static String methodToUpdateToDBCart = "UpdateToDBCart";
    public static String methodToGetCartTotalCount = "GetCartTotalCount";
    public static String methodToAddToDBCart = "AddToDBCart";
    public static String methodToGetBothCartDetail = "GetBothCartDetail";
    public static String methodToGetClearAllCart = "ClearAllCart";
    public static String methodToGetDeleteFromDBCart = "DeleteFromDBCart";
    public static String methodToAddToWishList = "AddToWishList";
    public static String methodToGetWishListProductDetail = "GetWishListProductDetail";
    public static String methodToDeleteFromWishlistAllProduct = "DeleteFromWishlistAllProduct";
    public static String methodToDeleteFromWishlistProduct = "DeleteFromWishlistProduct";
    public static String methodToAllProductImages = "ProductImages";
    public static String methodToCheck_StockPermission = "Check_StockPermission";
    public static String methodToCheck_AvailableStock = "Check_AvailableStock";
    public static String methodToCheckPinCode = "CheckPincodeService";
    public static String methodToGetMessageHistory = "MessageHistory";
    public static String methodToUpdateDeviceID = "DeviceDetail";
    public static String methodToGetDeviceTockenNo = "GetDeviceTockenNo";
    public static String methodToSendNotification_Order = "SendNotification_Order";
    public static String methodMaster_FillPackage = "LoadPackage";
    public static String methodCheckPinJoining = "CheckPinJoining";
    public static String methodToCheck_OTPPermission = "CheckJoiningOTPPremission";
    public static String methodToGetDashboard2Detail = "Dashboard2";
    public static String methodVerificationMobileNoJson = "VerificationMobileNo";
    public static String methodToGetWalletBalance = "GetAvailableWalletBalance";
    public static String methodToGetROI_MonthlyWalletShoppingLimit = "ROI_MonthlyWalletProductPurchaseLimit";
    public static String methodToGetROI_MonthlyWalletRechargeLimit = "ROI_MonthlyWalletRechargeLimit";
    public static String methodToGetPurchaseProductWalletDetails = "PurchaseProductWalletDetails";
    public static String methodToGetRechargeWalletDetails = "RechargeWalletDetails";
    public static String methodToFillFanBook = "FillFanBook";

}