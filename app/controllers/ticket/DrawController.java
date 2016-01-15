package controllers.ticket;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.jbarcode.JBarcode;
import org.jbarcode.paint.WidthCodedPainter;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.application.Application;
import models.common.Account;
import models.log.ShareLog;
import models.shake.ShakeRecord;
import models.share.ShareRecord;
import models.ticket.Coupon;
import models.ticket.CouponDetail;
import models.ticket.CouponMark;
import models.ticket.CouponRecord;
import models.ticket.DetailData;
import models.ticket.StatisticData;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sun.misc.BASE64Encoder;
import controllers.AppController;
import controllers.common.CodeGenerator;

public class DrawController extends Controller {
    private SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public Result getStatus(String shopId) {
        DynamicForm form = Form.form().bindFromRequest();
        String userId = form.get("userId");
        String appId = form.get("appId");
        Logger.info("shopId" + shopId);
        Logger.info("userId" + userId);
        Logger.info("appId" + appId);
        //Logger.info("1");
        ObjectNode node = Json.newObject();
        if (shopId == null) {
            //it is not from shop
            node.put("status", DRAW_STATUS.NO_SHAKE.ordinal());
            return ok(node);
        }
        //Logger.info("2");
        
        //find available coupon date
        List<Coupon> couponList = Coupon.find.where(String.format("app_id = '%s' group by batch", appId))                
                .findList();
        
        Logger.info("3");
        if (null == couponList || couponList.isEmpty()) {
            node.put("status", DRAW_STATUS.NO_ACTIVITY.ordinal());
            return ok(node);
        }
        
        Logger.info("4");
        //check whether it is in coupon date
        Date today = new Date();
        
        Coupon availableBatchCoupon = null;
        
        Logger.info("5");
        Calendar expiredDate = Calendar.getInstance();
        for (Coupon coupon : couponList) {
            
            if (coupon.available_time.before(today)) {
                expiredDate.setTime(coupon.available_time);
                expiredDate.add(Calendar.DATE, coupon.expired_in_days);
                
                if (expiredDate.getTime().after(today)) {
                    availableBatchCoupon = coupon;
                    break;
                }
            }
        }
        
        Logger.info("6");
        if (availableBatchCoupon == null) {
            node.put("status", DRAW_STATUS.NO_ACTIVITY.ordinal());
            return ok(node);
        }
        
        Logger.info("7");
        //check whether the customer is drawed
        List<CouponRecord> recordList = CouponRecord.find.where()
                .between("create_time", 
                        m_sdf.format(availableBatchCoupon.available_time), 
                        m_sdf.format(expiredDate.getTime()))
                .eq("user_id", userId)
                .findList();
        
        Logger.info("8");
        if (null != recordList && !recordList.isEmpty()) {
            //check whether the customer has used the coupon
            /*
            for (CouponRecord couponRecord : recordList) {
                if (!couponRecord.is_used) {
                    node.put("status", DRAW_STATUS.OK.ordinal());
                    node.put("type", couponRecord.coupon.type);
                    node.put("number", couponRecord.coupon.name);
                    
                    return ok(node);
                }
            }
            */
            Logger.info("9");
            //check whether the customer shared before
            List<ShareRecord> shareList = ShareRecord.find.where()
                    .between("create_time",
                        m_sdf.format(availableBatchCoupon.available_time), 
                        m_sdf.format(expiredDate.getTime()))
                    .eq("user_id", userId)
                    .findList();
           
            Logger.info("10");
            if(null != shareList && !shareList.isEmpty()) {
                //it has shared before
                if (recordList.size() >= 2) {
                    //it has been used
                    node.put("status", DRAW_STATUS.USED.ordinal());
                    return ok(node);
                }
            }
            else {
                //not shared
                Logger.info("11");
                node.put("status", DRAW_STATUS.NO_SHARE.ordinal());
                return ok(node);
            }
        }
        Logger.info("12");
        //check whether the customer is shaked
        //search shake record
        ShakeRecord record = ShakeRecord.find.where()
                .eq("shop_id", shopId)
                .eq("user_id", userId)
                .between("create_time",
                        m_sdf.format(availableBatchCoupon.available_time), 
                        m_sdf.format(expiredDate.getTime()))
                .order("create_time desc")
                .setMaxRows(1)
                .findUnique();
        
        Logger.info("13");
        if (record == null) {
            //not shaked
            node.put("status", DRAW_STATUS.NO_SHAKE.ordinal());
            return ok(node);
        }

        node.put("status", DRAW_STATUS.OK.ordinal());
        return ok(node);
    }
    
    public Result getDetail(String couponId) {
        CouponDetail detail = null;
        
        if (null == couponId) {
            return ok();
        }
        
        String sql = String.format("select coupon.id, coupon.name, coupon.type, "
                + "coupon.available_time, coupon.expired_in_days, "
                + "coupon.code, shop.name, coupon_record.is_used from coupon_record "
                + "join shake_record on shake_record.user_id = coupon_record.user_id "
                + "join coupon on coupon.id = coupon_record.coupon_id "
                + "join shop on shake_record.shop_id = shop.id "
                + "where shake_record.create_time <= coupon_record.create_time "
                + "and coupon.id = '%s' "
                + "order by shake_record.create_time desc limit 1;", couponId);
        
        RawSql rawSql = 
                RawSqlBuilder.parse(sql)
                .columnMapping("coupon.id", "id") 
                .columnMapping("coupon.name", "value")
                .columnMapping("coupon.type", "type")
                .columnMapping("coupon.available_time", "available_time")
                .columnMapping("coupon.expired_in_days", "expired_in_days")
                .columnMapping("coupon.code", "code")
                .columnMapping("shop.name", "shop_name")
                .columnMapping("coupon_record.is_used", "is_used")                
                .create();
        
        Query<CouponDetail> query = Ebean.find(CouponDetail.class);
        query.setRawSql(rawSql);
        
        detail = query.findUnique();  
        
        if (null != detail) {
            detail.expired_date = new Date(detail.available_time.getTime() + (detail.expired_in_days - 1) * 1440L * 60000L);
            return ok(Json.toJson(detail));
        }
        
        return ok();        
    }
    
    public Result getDrawList() {
        DynamicForm form = Form.form().bindFromRequest();
        
        String userId = form.get("userId");
        String appId = form.get("appId");
        
        Logger.info("userId1: " + userId);
        Logger.info("appId1:" + appId);
        
        List<CouponMark> couponList = new ArrayList<CouponMark>();
        
        if (userId == null || appId == null) {
            return ok(Json.toJson(couponList));
        }
        
        String sql = String.format(
                "select coupon.id, coupon.name, coupon.type, coupon.available_time, "
                + "coupon.expired_in_days, coupon.code, coupon_record.is_used from coupon "                
                + "join coupon_record on coupon_record.coupon_id = coupon.id "
                + "where coupon.app_id = '%s' and user_id = '%s';",
                appId, userId);
        
        Logger.info("sql:" + sql);
        RawSql rawSql = 
                RawSqlBuilder.parse(sql)
                .columnMapping("coupon.id", "id") 
                .columnMapping("coupon.name", "value")
                .columnMapping("coupon.type", "type")
                .columnMapping("coupon.available_time", "available_time")
                .columnMapping("coupon.expired_in_days", "expired_in_days")
                .columnMapping("coupon.code", "code")
                .columnMapping("coupon_record.is_used", "is_used")
                .create();
        
        
        Logger.info("15");
        Query<CouponMark> query = Ebean.find(CouponMark.class);
        query.setRawSql(rawSql);
        
        couponList = query.findList();     
        
        if (null != couponList) {
            for (CouponMark coupon : couponList) {
                coupon.expired_time = new Date(coupon.available_time.getTime() + (coupon.expired_in_days - 1) * 1440L * 60000L);
            }
        }
        return ok(Json.toJson(couponList));
    }
    
    public synchronized Result draw() {
        DynamicForm form = Form.form().bindFromRequest();
        String userId = form.get("userId");
        String appId = form.get("appId");
        
        ObjectNode node = Json.newObject();
        
        Logger.info("14");
        //try to draw a coupon that is not used
        //synchronized(this) {
        Date today = new Date();
        
            String sql = "select coupon.id, coupon.app_id, coupon.name, coupon.type, "
                    + "coupon.create_time, coupon.date_type, coupon.available_time, "
                    + "coupon.expired_in_days, coupon.code, coupon.picture, coupon.batch from coupon "
                    + "left join coupon_record on coupon_record.coupon_id = coupon.id "
                    + "where coupon_record.id is null "
                    + "and (to_days(DATE_FORMAT(now(),'%Y%m%d')) - to_days(DATE_FORMAT(coupon.available_time, '%Y%m%d'))) <= coupon.expired_in_days;";
                    
            RawSql rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("coupon.id", "id") 
                    .columnMapping("coupon.app_id", "app_id")
                    .columnMapping("coupon.name", "name")
                    .columnMapping("coupon.type", "type")
                    .columnMapping("coupon.create_time", "create_time")
                    .columnMapping("coupon.date_type", "date_type")
                    .columnMapping("coupon.available_time", "available_time")
                    .columnMapping("coupon.expired_in_days", "expired_in_days")
                    .columnMapping("coupon.code", "code")
                    .columnMapping("coupon.picture", "picture")
                    .columnMapping("coupon.batch", "batch")
                    .create();
            
            Logger.info("15");
            Query<Coupon> query = Ebean.find(Coupon.class);
            query.setRawSql(rawSql);
            
            List<Coupon> couponList = query.findList();     

            
            Logger.info("16");
            if (couponList == null  || couponList.isEmpty()) {
                node.put("status", DRAW_STATUS.NO_COUPON.ordinal());
                return ok(node);
            }
            
            int index = (int)(CodeGenerator.random.nextFloat() * couponList.size());
            if (index == couponList.size()) {
                index--;
            }
            
            Logger.info("17");
            Coupon chosenCoupon = couponList.get(index);
            
            CouponRecord couponRecord = new CouponRecord();
            couponRecord.id = CodeGenerator.GenerateUUId();
            couponRecord.coupon = chosenCoupon;
            couponRecord.account = Account.find.byId(userId);
            couponRecord.coupon_id = chosenCoupon.id;
            couponRecord.is_drawed = true;
            couponRecord.is_used = false;
            couponRecord.create_time = new Date();
            
            node.put("type", chosenCoupon.type);
            node.put("number", chosenCoupon.name);   
            node.put("id", couponRecord.id);
            
            Logger.info("18");
            try {
                Ebean.save(couponRecord);
            }
            catch (Throwable e) {
                Logger.info(e.getMessage());
                node.put("status", DRAW_STATUS.SYS_ERR.ordinal());
                return ok(node);
            }
            
        //}

       Calendar expiredDate = Calendar.getInstance();
       expiredDate.setTime(chosenCoupon.available_time);
       expiredDate.add(Calendar.DATE, chosenCoupon.expired_in_days);
            
        //check whether the customer shared before
       List<ShareRecord> shareList = ShareRecord.find.where()
                .between("create_time",
                m_sdf.format(chosenCoupon.available_time), 
                m_sdf.format(expiredDate.getTime()))
                .eq("user_id", userId)
                .findList();
           
        if(null != shareList && !shareList.isEmpty()) {
            node.put("status", DRAW_STATUS.SHARED_OK.ordinal());            
        }
        else {            
            node.put("status", DRAW_STATUS.OK.ordinal());
        }
            
        Logger.info("19");
       
        return ok(node);
        
        
    }
    
    public Result isCouponUsed(String couponId) {
        boolean is_used = true;
        try {
            CouponRecord couponRecord = CouponRecord.find.where()
                    .eq("coupon_id", couponId).findUnique();
            
            if (null != couponRecord) {
                is_used = couponRecord.is_used;
            }
            else {
                is_used = false;
            }
        }
        catch (Throwable e) {
            
        }
        
        return ok(Json.newObject().put("isUsed", is_used));
    }
    
    public Result useCoupon(String couponId) {
       
        try {
            CouponRecord couponRecord = CouponRecord.find.where()
                    .eq("coupon_id", couponId).findUnique();
            
            if (null != couponRecord) {
                couponRecord.is_used = true;
                
                Set<String> updateProps = new HashSet<String>();
                updateProps.add("is_used");
                Ebean.update(couponRecord, updateProps);
                
                return ok(Json.toJson(couponRecord));
            }            
        }
        catch (Throwable e) {
            Logger.info("error:" + e.getMessage());
        }

        return ok();
    }
    
    private enum DRAW_STATUS {
        OK,
        USED,
        NO_SHAKE,
        NO_ACTIVITY,
        NO_COUPON,
        SYS_ERR,
        NO_SHARE,
        SHARED_OK,
                
    }
    
    public Result getBarcode(String code) {
        byte[] data = null;  

        try   
        {  

            JBarcode localJBarcode = new JBarcode(
                org.jbarcode.encode.Code128Encoder.getInstance(),
                WidthCodedPainter.getInstance(),
                new org.jbarcode.paint.TextPainter() {

                    @Override
                    public void paintText(BufferedImage arg0, String arg1,
                            int arg2) {
                        // TODO Auto-generated method stub
                        
                    }}
                );

            BufferedImage img = localJBarcode.createBarcode(code);
        
            InputStream in = null;  

            ByteArrayOutputStream bs = new ByteArrayOutputStream();  
            ImageOutputStream imOut = ImageIO.createImageOutputStream(bs); 
            ImageIO.write(img, "jpg",imOut);
            
            in = new ByteArrayInputStream(bs.toByteArray());         
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        }   
        catch (Throwable e)   
        {  
            e.printStackTrace();  
        }  

        BASE64Encoder encoder = new BASE64Encoder();  
        return ok(encoder.encode(data));
        
    }
    
    public Result saveShareRecord() {
        DynamicForm form = Form.form().bindFromRequest();
        String userId = form.get("userId");
        String appId = form.get("appId");
        String url = form.get("url");
        
        try {
            ShareRecord shareRecord = new ShareRecord();
            shareRecord.id = CodeGenerator.GenerateUUId();
            shareRecord.app = Application.find.byId(appId);
            shareRecord.appId = appId;
            shareRecord.account = Account.find.byId(userId);
            shareRecord.userId = userId;
            shareRecord.share_url = url;
            shareRecord.create_time = new Date();
            
            Ebean.save(shareRecord);
            return ok();
        }
        catch (Throwable e) {
            Logger.info("erroR:" + e.getMessage());
        }
        
        return ok();
    }
    
    public Result saveShareLog() {
        DynamicForm form = Form.form().bindFromRequest();
        String domain = form.get("domain");
        String path = form.get("path");
        String userId = form.get("userId");
        String shareBy = form.get("SharedBy");
        
        try {
            ShareLog log = new ShareLog();
            log.domain = domain;
            log.path = path;
            log.userId = userId;
            log.shareBy = shareBy;
            log.create_time = new Date();
            
            Ebean.save(log);         
        }
        catch (Throwable e) {
            
        }
        
        return ok();
    }
    
    public Result getAllShopStatistics(String shopIds, String date) {
        String header = request().getHeader("Origin");
        header = (header == null ? "*" : header);
        response().setHeader("Access-Control-Allow-Origin", header);
        response().setHeader("Access-Control-Allow-Credentials", "true");
        response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
        
        String[] shops = shopIds.split(",");
        
        List<ObjectNode> nodeList = new ArrayList<ObjectNode>();
        
        ObjectNode node = null;
        
        for (String id : shops) {
            node = getShopStatisticsNode(id, date);
            
            if (null != node) {
                node.put("id", id);
                nodeList.add(node);
            }
        }
        
        return ok(Json.toJson(nodeList));
    }
    
    public Result getShopStatistics(String shopId, String date) {
        String header = request().getHeader("Origin");
        header = (header == null ? "*" : header);
        response().setHeader("Access-Control-Allow-Origin", header);
        response().setHeader("Access-Control-Allow-Credentials", "true");
        response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
        
        ObjectNode node = getShopStatisticsNode(shopId, date);
        
        if (null == node) {
            return status(404);
        }
        
        return ok(node);
        
    }
    
    public Result getShopDailyStatistics(String shopId, String fromDate, String toDate) {
        String header = request().getHeader("Origin");
        header = (header == null ? "*" : header);
        response().setHeader("Access-Control-Allow-Origin", header);
        response().setHeader("Access-Control-Allow-Credentials", "true");
        response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
        
        List<ObjectNode> dataList = new ArrayList<ObjectNode>();
        
        try {
            Calendar start = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            
            start.setTime(sdf.parse(fromDate));
            
            Calendar end = Calendar.getInstance();
            end.setTime(sdf.parse(toDate));
            
            ObjectNode data = null;
            while (!start.after(end)) {
                
                data = getShopStatisticsNode(shopId, sdf.format(start.getTime()));
                
                if (null != data) {
                    data.put("date", sdf.format(start.getTime()));
                    dataList.add(data);
                }
                
                start.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return ok(Json.toJson(dataList));
        
    }
    
    public ObjectNode getShopStatisticsNode(String shopId, String date) {
        String appId = session("appId");

        ObjectNode node = Json.newObject();
        
        try {
            
            String sql = String.format("select count(id) from shake_record where shop_id = '%s' and create_time >='%s' and create_time < date_add('%s', interval 1 day)", shopId, date, date);
            RawSql rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(id)", "value")
                    .create();
            
            Query<StatisticData> query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            StatisticData data = query.findUnique();
            node.put("ShakeCount", data.value);
            
            sql = String.format("select count(*) from (select count(id), shop_id from shake_record "
                    + "where shop_id = '%s' and create_time >= '%s' and create_time < date_add('%s', interval 1 day) group by user_id) t0", shopId, date, date);
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("ShakePersonCount", data.value);
            
            sql = String.format("select count(*) from coupon_record "
                    + "join shake_record on shake_record.user_id = coupon_record.user_id "
                    + "where shop_id = '%s' and "
                    + "coupon_record.create_time >= '%s' and "
                    + "coupon_record.create_time < date_add('%s', interval 1 day)", shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("CouponCount", data.value);
            
            sql = String.format("select count(*) from (select count(*) from coupon_record "
                    + "join shake_record on shake_record.user_id = coupon_record.user_id "
                    + "where shop_id = '%s' and coupon_record.create_time >= '%s' and "
                    + "coupon_record.create_time < date_add('%s', interval 1 day) "
                    + "group by coupon_record.user_id) t2", shopId, date, date);

            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("CouponPersonCount", data.value);
            
            sql = String.format("select count(*) from share_log "
                    + "join shake_record on share_log.user_id = shake_record.user_id "
                    + "where path = 'share.html' and shop_id = '%s' and "
                    + "shake_record.create_time >= '%s' and shake_record.create_time < date_add('%s', interval 1 day)", 
                    shopId, date, date);

            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("ExchangeCount", data.value);
            
            sql = String.format("select count(*) from (select count(*) from share_log "
                    + "join shake_record on share_log.user_id = shake_record.user_id "
                    + "where path = 'share.html' and shop_id = '%s' and "
                    + "shake_record.create_time >= '%s' and "
                    + "shake_record.create_time < date_add('%s', interval 1 day) "
                    + "group by shake_record.user_id) t4", shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("ExchangePersonCount", data.value);
            
            sql = String.format("select count(*) from share_record "
                    + "join shake_record on share_record.user_id = shake_record.user_id "
                    + "where shop_id = '%s' and share_record.create_time >= '%s' and "
                    + "share_record.create_time < date_add('%s', interval 1 day)", 
                    shopId, date, date);

            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("ShareCount", data.value);
            
            sql = String.format("select count(*) from (select count(*) from share_record "
                    + "join shake_record on share_record.user_id = shake_record.user_id "
                    + "where shop_id = '%s' and share_record.create_time >= '%s' and "
                    + "share_record.create_time < date_add('%s', interval 1 day) "
                    + "group by share_record.user_id) t6", shopId, date, date);

            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query = Ebean.find(StatisticData.class);
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("SharePersonCount", data.value);
            
        }
        catch (Throwable e) {
            node = null;
        }
        
        return node;
    }
    
    public Result getShopDetails(String shopId, String date) {
        String appId = session("appId");
        
        String header = request().getHeader("Origin");
        header = (header == null ? "*" : header);
        response().setHeader("Access-Control-Allow-Origin", header);
        response().setHeader("Access-Control-Allow-Credentials", "true");
        response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
        
        try {
            
            ObjectNode node = Json.newObject();
            Logger.info("1");
            String sql = String.format("select create_time, number from ("
                    + "select date_format(create_time, '%%H:00:00') as create_time, count(id) as number from shake_record "
                    + "where shop_id = '%s' and create_time >= '%s' and "
                    + "create_time < date_add('%s', interval 1 day) "
                    + "group by date_format(create_time, '%%H:00:00') order by create_time) t0", shopId, date, date);

            RawSql rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            Query<DetailData> query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            List<DetailData> dataList = query.findList();
            
            node.put("ShakeCount", Json.toJson(dataList));
            
            Logger.info("2");            
            sql = String.format("select create_time, number from ("
                    + "select date_format(create_time, '%%H:00:00') as create_time, count(id) as number from "
                    + "(select create_time , id from shake_record "
                    + "where shop_id = '%s' and create_time >= '%s' and "
                    + "create_time < date_add('%s', interval 1 day) "
                    + "group by user_id order by create_time) t0 "
                    + "group by date_format(create_time, '%%H:00:00')) t1", shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("ShakePersonCount", Json.toJson(dataList));    
            
            Logger.info("3");
            sql = String.format("select create_time, number from ("
                    + "select date_format(coupon_record.create_time, '%%H:00:00') as create_time, count(*) as number from coupon_record "
                    + "join shake_record on shake_record.user_id = coupon_record.user_id where shop_id = '%s' "
                    + "and coupon_record.create_time >= '%s' and coupon_record.create_time < date_add('%s', interval 1 day) "
                    + "group by date_format(coupon_record.create_time, '%%H:00:00') order by create_time) t0;", 
                    shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("CouponCount", Json.toJson(dataList));
            
            Logger.info("4");
            sql = String.format("select create_time, number from ("
                    + "select date_format(create_time, '%%H:00:00') as create_time, count(id) as number from "
                    + "(select coupon_record.create_time create_time, coupon_record.id id from coupon_record "
                    + "join shake_record on shake_record.user_id = coupon_record.user_id "
                    + "where shop_id = '%s' and coupon_record.create_time >= '%s' and "
                    + "coupon_record.create_time < date_add('%s', interval 1 day) "
                    + "group by coupon_record.user_id order by create_time) t0 "
                    + "group by date_format(create_time, '%%H:00:00')) t1", shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("CouponPersonCount", Json.toJson(dataList));
            
            Logger.info("6");
            sql = String.format("select create_time, number from ("
                    + "select date_format(shake_record.create_time, '%%H:00:00') as create_time, count(*) as number from share_log "
                    + "join shake_record on share_log.user_id = shake_record.user_id where path = 'share.html' and "
                    + "shop_id = '%s' and shake_record.create_time >= '%s' and "
                    + "shake_record.create_time < date_add('%s', interval 1 day) "
                    + "group by date_format(shake_record.create_time, '%%H:00:00') order by create_time) t0;", 
                    shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("ExchangeCount", Json.toJson(dataList));
            
            Logger.info("7");
            sql = String.format("select create_time, number from ("
                    + "select date_format(create_time, '%%H:00:00') as create_time, count(id) as number from "
                    + "(select shake_record.create_time create_time, share_log.id id from share_log "
                    + "join shake_record on share_log.user_id = shake_record.user_id where path = 'share.html' "
                    + "and shop_id = '%s' and shake_record.create_time >= '%s' and "
                    + "shake_record.create_time < date_add('%s', interval 1 day) "
                    + "group by shake_record.user_id order by create_time) t0 "
                    + "group by date_format(create_time, '%%H:00:00')) t1;", 
                    shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("ExchangePersonCount", Json.toJson(dataList));
            
            Logger.info("8");
            sql = String.format("select create_time, number from ("
                    + "select date_format(share_record.create_time, '%%H:00:00') as create_time, count(*) as number from share_record "
                    + "join shake_record on share_record.user_id = shake_record.user_id "
                    + "where shop_id = '%s' and share_record.create_time >= '%s' and "
                    + "share_record.create_time < date_add('%s', interval 1 day) "
                    + "group by date_format(share_record.create_time, '%%H:00:00') "
                    + "order by create_time) t0;", shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("ShareCount", Json.toJson(dataList));
            
            Logger.info("9");
            sql = String.format("select create_time, number from ("
                    + "select date_format(create_time, '%%H:00:00') as create_time, count(id) as number from "
                    + "(select share_record.create_time create_time, share_record.id from share_record "
                    + "join shake_record on share_record.user_id = shake_record.user_id "
                    + "where shop_id = '%s' and share_record.create_time >= '%s' and "
                    + "share_record.create_time < date_add('%s', interval 1 day) "
                    + "group by share_record.user_id order by share_record.create_time) t0 "
                    + "group by date_format(create_time, '%%H:00:00')) t1;", shopId, date, date);
            
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("create_time", "time")
                    .columnMapping("number", "value")
                    .create();
            
            query = Ebean.find(DetailData.class);
            query.setRawSql(rawSql);
            
            dataList = query.findList();
            
            node.put("SharePersonCount", Json.toJson(dataList));            
          
            return ok(node);
        }
        catch (Throwable e) {
            Logger.info(e.getMessage());
            return status(404);
        }        
    }    
}
