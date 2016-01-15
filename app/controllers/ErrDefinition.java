/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: Definitions of all err codes.
 */
package controllers;

public class ErrDefinition {
	//ACCOUNT ERROR
	public static final int E_ACCOUNT_UNAUTHENTICATED = 500;
	public static final int E_ACCOUNT_INCORRECTPARAM  = 501;
	public static final int E_ACCOUNT_ALREADY_EXIST   = 502;
	public static final int E_ACCOUNT_UNKNOWN_ERR     = 503;
	public static final int E_ACCOUNT_NO_PASSWORD     = 504;
	public static final int E_ACCOUNT_NOT_FOUND       = 505;
	public static final int E_ACCOUNT_NO_RIGHT        = 506;
	
	//APPLICATION ERROR
	public static final int E_APP_FORM_HASERROR       = 510;
	public static final int E_APP_EXCEPTION_FOUND     = 511;
	public static final int E_APP_NOT_FOUND           = 512;
	public static final int E_APP_IMG_INVALID_APP     = 513;
	public static final int E_APP_IMG_INVALID_USER    = 514;
	public static final int E_APP_IMG_NODATA          = 515;
	public static final int E_APP_UPLOAD_FAILED       = 516;
	public static final int E_APP_NOT_SELECTED        = 517;
	public static final int E_APP_IMG_FAILED          = 518;
	public static final int E_APP_IMG_TOOLARGE        = 519;
	
	//BANNER ERROR
	public static final int E_BANNER_FORM_HASERROR    = 520;
	public static final int E_BANNER_CREATE_FIALED    = 521;
	public static final int E_BANNER_NOT_FOUND        = 522;
	public static final int E_BANNER_READ_FAILED      = 523;
	public static final int E_BANNER_UPDATE_FAILED    = 524;
	public static final int E_BANNER_DELETE_FAILED    = 525;
	
	//APP PROFILE ERROR
	public static final int E_APPPROFILE_FORM_HASERROR     = 530;
	public static final int E_APPPROFILE_SESSION_INCORRECT = 531;
	public static final int E_APPPROFILE_CREATE_FAILED     = 532;
	public static final int E_APPPROFILE_NOT_FOUND         = 533;
	public static final int E_APPPROFILE_READ_FAILED       = 534;
	public static final int E_APPPROFILE_UPDATE_FAILED     = 535;
	public static final int E_APPPROFILE_DELETE_FAILED     = 536;
	
	//INTEREST FAN ERROR
	public static final int E_INTERESTFAN_FORM_HASERROR    = 540;
	public static final int E_INTERESTFAN_SAME_ACCOUNT     = 541;
	public static final int E_INTERESTFAN_CREATE_FAILED    = 542;
	public static final int E_INTERESTFAN_READ_FAILED      = 543;
	public static final int E_INTERESTFAN_UPDATE_FAILED    = 544;
	public static final int E_INTERESTFAN_DELETE_FAILED    = 545;
	public static final int E_INTERESTFAN_NOT_FOUND        = 546;
	
	//PROGRAM ERROR
	public static final int E_PROGRAM_FORM_HASERROR   = 550;
	public static final int E_PROGRAM_CREATE_FAILED   = 551;
	public static final int E_PROGRAM_READ_FAILED     = 552;
	public static final int E_PROGRAM_UPDATE_FAILED   = 553;
	public static final int E_PROGRAM_DELETE_FAILED   = 554;
	
	//PROGRAM CONTENT ERROR
	public static final int E_PROGRAM_CONTENT_FORM_HASERROR = 560;
	public static final int E_PROGRAM_CONTENT_CREATE_FAILED = 561;
	public static final int E_PROGRAM_CONTENT_READ_FAILED   = 562;
	public static final int E_PROGRAM_CONTENT_UPDATE_FAILED = 563;
	public static final int E_PROGRAM_CONTENT_DELETE_FAILED = 564;
	
	//ACTIVITY ERROR
	public static final int E_ACTIVITY_FORM_HASERROR   = 570;
	public static final int E_ACTIVITY_CREATE_FAILED   = 571;
	public static final int E_ACTIVITY_READ_FAILED     = 572;
	public static final int E_ACTIVITY_UPDATE_FAILED   = 573;
	public static final int E_ACTIVITY_DELETE_FAILED   = 574;
	public static final int E_ACTIVITY_INVALID_TIME    = 575;
	public static final int E_ACTIVITY_NOT_SAME_APP    = 576;
	
	//ACTIVITY CONTENT ERROR
	public static final int E_ACTIVITY_CONTENT_FORM_HASERROR = 580;
	public static final int E_ACTIVITY_CONTENT_CREATE_FAILED = 581;
	public static final int E_ACTIVITY_CONTENT_READ_FAILED   = 582;
	public static final int E_ACTIVITY_CONTENT_UPDATE_FAILED = 583;
	public static final int E_ACTIVITY_CONTENT_DELETE_FAILED = 584;
	
	//ACTIVITY CHOICE ERROR
	public static final int E_ACTIVITY_CHOICE_FORM_HASERROR  = 590;
	public static final int E_ACTIVITY_CHOICE_CREATE_FAILED  = 591;
	public static final int E_ACTIVITY_CHOICE_READ_FAILED    = 592;
	public static final int E_ACTIVITY_CHOICE_UPDATE_FAILED  = 593;
	public static final int E_ACTIVITY_CHOICE_DELETE_FAILED  = 594;
	
	//ACTIVITY RESULT ERROR
	public static final int E_ACTIVITY_RESULT_FORM_HASERROR  = 600;
	public static final int E_ACTIVITY_RESULT_CREATE_FAILED  = 601;
	public static final int E_ACTIVITY_RESULT_READ_FAILED    = 602;
	public static final int E_ACTIVITY_RESULT_UPDATE_FAILED  = 603;
	public static final int E_ACTIVITY_RESULT_DELETE_FAILED  = 604;
	
	//ALBUM ERROR
	public static final int E_ALBUM_FORM_HASERROR  = 610;
	public static final int E_ALBUM_CREATE_FAILED  = 611;
	public static final int E_ALBUM_READ_FAILED    = 612;
	public static final int E_ALBUM_UPDATE_FAILED  = 613;
	public static final int E_ALBUM_DELETE_FAILED  = 614;
	public static final int E_ALBUM_CREATE_EXCESS_MAX = 615;
	
	//CONSTELLATION ERROR
	public static final int E_CONSTELLATION_READ_FAILED = 620;
	public static final int E_NATIONALITY_READ_FAILED   = 621;
	
	//GIFT ERROR
	public static final int E_GIFT_READ_FAILED = 630;
	public static final int E_GIFT_GIVE_FAILED = 631;
	public static final int E_GIFT_LACK_MONEY = 632;
	public static final int E_GIFT_LACK_ID = 633;
	public static final int E_GIFT_TABLE_ERROR = 634;
	
	//HONOR ERROR
	public static final int E_HONOR_READ_FAILED = 635;
	
	//CHARM ERROR
	public static final int E_CHARM_READ_FAILED = 636;
	
	//MONEY ACCOUNT ERROR
	public static final int E_MONEY_ACCOUNT_FAILED=638;
	public static final int E_MONEY_SAVE_FAILED=639;
	
	//GEO ERROR
	public static final int E_GEO_READ_FAILED = 640;
	
	//SHOP ERROR
	public static final int E_SHOP_FORM_ERROR   = 650;
	public static final int E_SHOP_CREATE_ERROR = 651;
	public static final int E_SHOP_READ_ERROR   = 652;
	public static final int E_SHOP_UPDATE_ERROR = 653;
	public static final int E_SHOP_DELETE_ERROR = 654;
	
	//Shake Record
	public static final int E_SHAKE_RECORD_FORM_ERROR = 660;
	public static final int E_SHAKE_RECORD_CREATE_ERROR = 661;
	public static final int E_SHAKE_RECORD_READ_ERROR = 662;
	public static final int E_SHAKE_RECORD_UPDATE_ERROR = 663;
	public static final int E_SHAKE_RECORD_DELETE_ERROR = 664;
	
	//Coupon
	public static final int E_COUPON_FORM_ERROR = 670;
	public static final int E_COUPON_CREATE_ERROR = 671;
	public static final int E_COUPON_READ_ERROR = 672;
	public static final int E_COUPON_UPDATE_ERROR = 673;
	public static final int E_COUPON_DELETE_ERROR = 674;
	
	//Coupon Record
	public static final int E_COUPON_RECORD_FORM_ERROR = 680;
	public static final int E_COUPON_RECORD_CREATE_ERROR = 681;
	public static final int E_COUPON_RECORD_READ_ERROR = 682;
	public static final int E_COUPON_RECORD_UPDATE_ERROR = 683;
	public static final int E_COUPON_RECORD_DELETE_ERROR = 684;
	
	//User Address
	public static final int E_USER_ADDRESS_FORM_ERROR = 690;
	public static final int E_USER_ADDRESS_CREATE_ERROR = 691;
	public static final int E_USER_ADDRESS_READ_ERROR = 692;
	public static final int E_USER_ADDRESS_UPDATE_ERROR = 693;
	public static final int E_USER_ADDRESS_DELETE_ERROR = 694;

	public static final int E_ARTICLE_FORM_ERROR = 700;
	public static final int E_ARTICLE_POST_ERROR = 701;
	public static final int E_ARTICLE_NO_THEME_ERROR = 702;
	public static final int E_ARTICLE_READ_ERROR = 703;
	public static final int E_ARTICLE_UPDATE_ERROR = 704;
	public static final int E_ARTICLE_DELETE_ERROR = 705;
		
	public static final int E_COMMENT_FORM_ERROR = 710;
	public static final int E_COMMENT_POST_ERROR = 711;
	public static final int E_COMMENT_READ_ERROR = 712;
	public static final int E_COMMENT_UPDATE_ERROR = 713;
	public static final int E_COMMENT_DELETE_ERROR = 714;
		
	public static final int E_THUMBS_HAS_DONE_ERROR = 720;
	public static final int E_THUMBS_SUBMIT_ERROR = 721;
	public static final int E_COLLECTIONS_POST_ERROR=722;
	public static final int E_COLLECTIONS_GET_ERROR=723;
		
	public static final int E_THEME_VIEW_ERROR = 730;
	public static final int E_THEME_ENTER_ERROR = 731;
	public static final int E_THEME_JOIN_ERROR = 732;
	public static final int E_THEME_EXIT_ERROR = 733;
	public static final int E_THEME_MYSELF_ERROR = 734;
	public static final int E_THEME_READ_ERROR = 735;
	public static final int E_THEME_FORM_ERROR=736;
	public static final int E_THEME_CREATE_ERROR=737;
	public static final int E_THEME_DELETE_ERROR=738;
	
	//entry form   
	public static final int E_ENTRY_CREAT_ERROR=744;
	public static final int E_ENTRY_READ_ERROR=745;
	
	public static final int E_UPLOADVIDEO_FAILED=754;
	public static final int E_EDUCATION_READ_FAILED=755;
    public static final int E_BEAUTIFUL_READ_FAILED=756;
    public static final int E_TYPE_READ_FAILED=757;
    public static final int E_ENTRY_UPLOAD_FAILED=758;
    public static final int E_JIABIN_READ_FAILED=759;
    		
    // chatroom announcement
    public static final int E_ANNOUNCEMENT_CREAT_ERROR=760;
    public static final int E_ANNOUNCEMENT_READ_FAILED=761;
    
    public static final int E_SIGN_IN_ERROR=770;
    public static final int E_IS_FOLLW_ERROR=771;
    
    //hotpeople 
    public static final int E_HOT_PIC_HASERROR=775;
    public static final int E_HOT_MP3_HASERROR=776;
    public static final int E_HOT_RANK_HASERROR=777;
    public static final int E_HOT_RANK_EXIST=778;
    public static final int E_HOT_PIC_DELETE_FAILED=779;
    public static final int E_HOT_RANK_DELETE_FAILED=780;
    public static final int E_HOT_MP3_DELETE_FAILED=781;
    
    //stock
    public static final int E_STOCK_CREATE_FAILED=790;
    public static final int E_STOCK_RECOMMEND_FAILED=791;
    public static final int E_STOCK_RANK_READ_FAILED=792;
    public static final int E_STOCK_WEEKEND_FAILED=793;
    
    //eat
    public static final int E_QUESTION_FIND_ERROR = 800;
    
    //vote
    public static final int E_VOTE_ERROR = 811;
    public static final int E_FOLLOW_ERROR=812;
    public static final int E_VOTE_FORM_ERROR=813;
    public static final int E_WEITCHID_ERROR=814;
    public static final int E_VOTEREAD_ERROR=815;
    public static final int E_VOTE_JOIN_ERROR=816;
}
