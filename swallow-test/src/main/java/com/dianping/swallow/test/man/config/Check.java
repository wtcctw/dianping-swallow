package com.dianping.swallow.test.man.config;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mengwenchao
 *
 * 2015年7月3日 下午4:31:06
 */
public class Check {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String config = "default=mongodb://10.1.101.155:27017,10.1.101.157:27017; tg-movie-ordering-notice,BC-SA-Creation-MEMBER,TGMobile_AdvertNewUser,TGMobile_Common,TGMobile_PassbookPush,TGMobile_PassbookUpdate,TGMobile_iPhonePush,ad_push,ad_push_back,ba-base-message-create,ba-base-message-read,ba-crm-mobile-messages,ba-push-device,ba-push-notification,ba_ecp_partner_data_processor,ba_fs_booking_invoice_noticeResult,ba_pc_ad_adapter,bc_web_sms,booking_invoice_apply,booking_unipay_noticePay,com_dianping_third_feed,com_dianping_third_feed_result,dp_action,dp_poi_combineshop,dp_poi_shopstatus,example,example2,fs-crm-bankaccount-abnormal,k2-request,listener_test,merchant_operation_log,paycenter_bookingChargeNotify_unipay,paycenter_bookingNotify_pos,paycenter_bookingNotify_pos_return,pc_delay,pc_merchant_confirm,pc_workflow,picture_quality_score_result,picture_quality_source,picture_resource_delete,puma_status_action_event,puma_status_event,puma_task_delete_event,puma_task_event,review_add_thirdparty,review_update_rank,rs_arrive_notify_api,rs_eventdriver,rs_notify_openapi,rs_tgReceiptNotify,rs_tgorderid,shop_resouce_action,sms-receive,takeaway-voice,tiaoshi_action,topic_midas_console,topic_midas_dynamicconfig,topic_midas_job,topic_midas_launch,topic_midas_order,topic_midas_synchronizer,tuangou_cps_1,tuangou_cps_notify_1,tuangou_operation_api_config_update,tuangou_remote_notify_order,tuangou_tracking_1,tuangou_tracking_weixin_message,verify_result_action,wedding_consume_notice,wedding_paycenter_result,wedding_unipay_noticePay,wizard-action,takeaway_selforder,TGSettle_Bill_Notify,Garisson_Call_End,customer_bank_account,wx_dpkb_text,midas_resourcebooking_apollo_shopconfirm,city_movie_sort_update,pp_bank_account_result,credit_decisionType,hroa_terminate_talk,hroa_terminate_talk,dp_apollo_shop_change_notify,dp_follow,takeaway_thirdpartyorder,mp_poi,ba_task_center_api,review_recommend,shanhui_refund_order,bank_account_verify_result,rs_jobs,ack_fun_settle_group,ack_fun_settle_result,update_movie_comment,paycenter_channel_transfer_notify,push-server-callback,movie_order_auto_refund,csc_case_allocation,technician_add,arts_apollotpda,tpd_deal_will_offline,credit_rule_result,crm_customer_shop_change,arts_apollo,ba_idp_promoteplan,dp_gift_message,paycenter_orderpaymentforcx_unipay,tp_uuid_log,account_action,movie_order_refund_mock,dpclub_sync_order,credit_computefwk_heartbeatType,credit_computefwk_groupType,dp_account_change,dp_account_plan_offline,pay_movie_ticket,tp_task_mq_task_created_closed,tp_task_mq_task_created,tp_task_mq_task_closed,consume_account_settle,dp_apollo_pos_notify,movie_ticket_issuance_notify,ack_hobbit_settle_result,hobbit_order_settleMsg,hobbit_order_settleMsgAck,apollo-message-mq-create,unicashier_create_order,unicashier_pay_order,unicashier_status_order=mongodb://10.1.6.31:27018,10.1.6.32:27018;paycenter_paymentNotify_order_order,paycenter_paymentNotify_order_pay,paycenter_paymentNotifyResult_pay,paycenter_paymentNotify_unipay,paycenter_paymentNotify_unipay_new,paycenter_paymentNotify_engine,paycenter_paymentNotify_engine_new,paycenter_paymentNotify_channel,paycenter_paymentNotify_channel_new,paycenter_dbankNotify_channel,paycenter_paymentNotifyResult_pay_movie,paycenter_bookingNotify_unipay,paycenter_bookingNotifyResult_booking,paycenter_paymentNotify_order_orderGroup,paycenter_riskInfoLog,fs-prepaidcard-activity-notice,fs-exchange-orderstatus-notice,tuangou_deal_currentjoin,tuangou_receipt_verify_success_notify,DP_USESR_LOGIN_MAINAPP,merchant_app_msg,fs-prepaidcard-bp-pending,fs-settle-tg-pp-created,fs-exchange-order-pay-notify,fs-settle-pp-pay-notify,mpos_paid,paycenter_paymentNotify_order_status,rs_message_notify_weixin,weixin_public_message,example_pay,deal_service_currentJoin_update,TGMobile_VS,rs_eda_topic,tg_cachemng_prodChangeNotify,mopay_order,pc_retry_message,deal_detail_update,dp_merchant_message_center,third_party_docking,takeaway-elemeorder,tuangou_receipt_create,paycenter_discount_issueResult,paycenter_discount_issueResult_detail,picture_status_change,shop_best_picture,paycenter_thirdparty_syncorder_result,paycenter_thirdparty_syncorder,paycenter_thirdparty_refund_result,paycenter_submit_order,paycenter_paymentNotify_unipay_ad,paycenter_paymentNotifyResult_unipay_ad,dp_credit_syncpay=mongodb://10.1.6.186:27017,10.1.6.188:27107;TuanGouRealTimeUpdate,arts_tuangou,UserMapRealTimeUpdate,arts_usermap,arts_shop,arts_shopdetail,arts_apollorotate,adfeedback,dp_apollo_solution_notify,com_dianping_cacheadmin2=mongodb://10.1.115.11:27017,10.1.115.12:27017;paycenter_submit_order_seckill,paycenter_submit_order_tuangou,paycenter_submit_order=mongodb://10.1.101.155:27018,10.1.101.157:27018;";
		
		Set<String> topics = new HashSet<String>();
		
		for(String split : config.split("\\s*;\\s*")){
			
			String []str = split.split("\\s*=\\s*");
			
			for(String topic : str[0].split("\\s*,\\s*")){

				if(topics.contains(topic)){
					System.out.println("dup:" + topic);
				}
				topics.add(topic);
			}
			
			
			
		}
	}

}
