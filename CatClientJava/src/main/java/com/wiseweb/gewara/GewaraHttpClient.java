package com.wiseweb.gewara;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.client.util.HttpClien;
import com.wiseweb.json.JSONObject;
import com.wiseweb.weibo.WeiboOauth;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Administrator on 2016/1/19.
 */
public class GewaraHttpClient extends HttpClien {
    public static GewaraHttpClient gewaraHttpClient;
    public static GewaraHttpClient getGewaraHttpClient(){
        gewaraHttpClient = gewaraHttpClient ==null?new GewaraHttpClient(): gewaraHttpClient;
        return gewaraHttpClient;
    }
    public static void main(String[] ages) {
        GewaraHttpClient gewaraHttpClient = GewaraHttpClient.getGewaraHttpClient();
        JSONObject account = new JSONObject();
        account.put(Constants.ACCOUNT_USERNAME, "xnzjucuosg@sina.cn");
        account.put(Constants.ACCOUNT_PASSWORD, "bbccdd1234");
        JSONObject Cookie = new JSONObject();
        Cookie.put("SUS", "SID-3237618061-1440992396-GZ-jo67y-e6a7890c99b35c558e9d9507b2e3e2ff");
        Cookie.put("SUB", "_2A25457zcDeTxGeVM6FUX8SbMzT2IHXVYK8SUrDV_PEJb6dM2YiGXLRHCoEsCZA5DBUt7DYWTj-HMo2o.");
        Cookie.put("tgc", "TGT-MzIzNzYxODA2MQ==-1440992396-gz-D2F42FEC5E83DF4EB1E37E3657F1898D");
        Cookie.put("SUE", "es%3D518488ce7367754ab1ca0a1ace8a9e1d%26ev%3Dv1%26es2%3D34d953409b643cbe65bb9d00c0a3d931%26rs0%3DFXC1Ku2xHtksb%252Bfsp%252B3SMnoDNtefpRkdMzCVdT%252FdTVRnDFWVT%252Fa2a37GEHhZFUiuXzNyFGHiy2yhUAtyquDscbH3WbhyXGg2cvTI7J7QCxOcozgGGATSnog2%252FtkF2cUEiCvqdTtMxRMUJUOZIOM10f6z3%252FKFukgqsYpCAzqLMA8%253D%26rv%3D0");
        Cookie.put("SUBP", "0033WrSXqPxfM725Ws9jqgMF55529P9D9W5UHx.nf.4CrxPElGdOg5-W");
        Cookie.put("SUP", "cv%3D1%26bt%3D1440992396%26et%3D1441078796%26d%3D40c3%26i%3De2ff%26us%3D1%26vf%3D%26vt%3D%26ac%3D%26st%3D0%26lt%3D1%26uid%3D3237618061%26user%3Dxnzjucuosg.cn%26ag%3D8%26name%3Dxnzjucuosg%2540sina.cn%26nick%3D%25E7%25BE%258E%25E7%25BE%258E%25E7%259A%2584%25E5%258C%2585%25E7%25A7%259F%25E5%25A9%2586%26sex%3D%26ps%3D0%26email%3Dxnzjucuosg%2540sina.cn%26dob%3D%26ln%3D%26os%3D%26fmp%3D%26lcp%3D2014-09-13%252007%253A49%253A02");
//        JSONObject result = gewaraHttpClient.gewaraWeiboOauth(Cookie,account);
//        if(!result.isNull("Cookie")){
//            Cookie = result.getJSONObject("Cookie");
//            result.remove("Cookie");
//            result = gewaraHttpClient.comment(Cookie,"哈哈","278071739",10);
//        }
//        gewaraHttpClient.info(result.toString());
        gewaraHttpClient.test();
    }
    public void test(){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://www.weibo.com");
            headers.put("Cookie","SUS=SID-3032681302-1453134657-GZ-1hgv6-ebd08146826b30c502bd401c755ebaa9; SUE=es%3Da330175cf0924e0feae6e813178d9c84%26ev%3Dv1%26es2%3Df2e1d33095f31a767019f83e7a21ee0d%26rs0%3DCu3olR3sGN5ajqtBiYy9vmhwZVOF9d3BGVtmN1JQu6QVi5mC%252FxMBm7m2%252Bamov2GtEeZ%252F6BoLczIlG9WtxTZlnOMR4zWFtW2k1bvQtLdQErw6GHEd8redDeGEXo0geRUvr1hwsuChWlBmm3bg9NmgdB0vVA14Pggq3965Ta1BMLY%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1453134656%26et%3D1453221056%26d%3Dc909%26i%3Dbaa9%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D0%26st%3D0%26uid%3D3032681302%26name%3Dwisewebtest%2540sina.com%26nick%3D%25E5%25BC%25A0%25E6%2598%258E%25E6%2588%2590%26fmp%3D%26lcp%3D2015-12-09%252015%253A40%253A27; SUB=_2A257mWMQDeTxGeVO6FAX-C_Pyz6IHXVY79PYrDV8PUNbuNBeLW_DkW9LHesQvafWxqsFb_h1XU_wORCV5UHjOA..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5iDzlir5e62s38nE.9anWZ5JpX5K2t; SUHB=0LcCur89fUs8Pl");
            HttpResponse response = Request(null, headers, "http://www.weibo.com/1685771263/Dd4l35YxK");
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                String content = getEntity(response);
                Elements e = Jsoup.parse(content).getElementsByTag("script");
                for(int i=0;i<e.size();i++){
                    if(e.get(i).html().contains("pl.content.weiboDetail.index")){
                        String html = e.get(i).html();
                        html = html.split("FM.view\\(")[1].trim();
                        html = html.substring(0, html.length() - 1);
                        JSONObject j = new JSONObject(html);
                        html = j.getString("html");
                        info(j.getString("domid"));
                        Document doc = Jsoup.parse(html);
                        e = doc.getElementsByAttributeValue("node-type","like_status");
                        String support = e.get(e.size()-1).text();
                        String comment = doc.getElementsByAttributeValue("node-type","comment_btn_text").text();
                        String forward = doc.getElementsByAttributeValue("node-type","forward_btn_text").text();
                        info(forward+"--"+comment+"--"+support);
                    }
                }
            }

        }catch (Exception e){
            error("test()异常",e);
        }
    }


    /**
     * 评论支持
     * @param Cookie
     * @param id
     * @return
     */
    public JSONObject commentSupport(JSONObject Cookie,String id){
        try {
            String url = "http://m.gewara.com/touch/wala/praise.xhtml?id="+id;
            return gewaraHttpRequest(null,Cookie,url,"评论支持");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关注他人
     * @param Cookie
     * @param attentionid
     * @return
     */
    public JSONObject addAttention(JSONObject Cookie,String attentionid){
        try {
            JSONObject form = new JSONObject();
            form.put("attentionid", attentionid);
            String url = "http://www.gewara.com/activity/community/ajax/addAttention.xhtml";
            return gewaraHttpRequest(form,Cookie,url,"关注他人");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 期待
     * @param Cookie
     * @param movieId
     */
    public JSONObject addCollection(JSONObject Cookie,String movieId){
        try {
            String url = "http://m.gewara.com/touch/movie/addCollection.xhtml?movieId="+movieId;
            return gewaraHttpRequest(null,Cookie,url,"期待");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 评论回复
     * @param Cookie
     * @param text
     * @param relatedid
     */
    public JSONObject commentReply(JSONObject Cookie,String text,String relatedid){
        try {
            String url = "http://m.gewara.com/touch/wala/reply.xhtml?relatedid="+relatedid+"&tag=movie&micrbody="+text;
            return gewaraHttpRequest(null,Cookie,url,"回复评论");
        } catch (Exception e) {
            error("commentReply(JSONObject Cookie,String text,String relatedid)异常",e);
        }
        return null;
    }

    /**
     * 评论
     * @param Cookie
     * @param text
     * @param movieId  电影ID
     * @param score  评分
     * @return
     */
    public JSONObject comment(JSONObject Cookie, String text, String movieId,String score) {
        try {
            JSONObject form = new JSONObject();
            form.put("relatedid",movieId);
            form.put("marks",score);
            form.put("tag","movie");
            form.put("micrbody", text);
            String url = "http://m.gewara.com/touch/wala/save.xhtml";
            return gewaraHttpRequest(form,Cookie,url,"评论");
        } catch (Exception e) {
            error("comment(JSONObject Cookie, String text, String movieId,int score)异常", e);
        }
        return null;
    }

    /**
     *  gewara统一网络请求
     * @param form
     * @param Cookie
     * @param url
     * @param type
     * @return
     */
    public JSONObject gewaraHttpRequest(JSONObject form, JSONObject Cookie, String url, String type){
        try{
            JSONObject headers = new JSONObject();
            headers.put("Cookie",Cookie);
            HttpResponse response = Request(form, headers, url);
            return handleResponse(response, type);
        } catch (Exception e) {
            error("gewaraHttpRequest(JSONObject form,JSONObject headers,String url,String type)异常", e);
        }
        return null;
    }

    public JSONObject handleResponse(HttpResponse response,String type){
        JSONObject result = new JSONObject();
        try{
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                String content = getEntity(response);
                if(content!=null && content.contains("success")){
                    info(content);
                    if(content.contains("=")){
                        content = content.split("=")[1].trim();
                    }
                    JSONObject j = new JSONObject(content);
                    boolean b = j.getBoolean("success");
                    result.put(Constants.STATUS,b?Constants.TASK_STATUS_COMPLETE:Constants.TASK_STATUS_FAILED);
                    result.put(Constants.CODE_REMARK,j.isNull("html")?(b?"成功":"失败") : j.get("html"));
                }else{
                    result.put(Constants.CODE_REMARK,"返回错误结果："+content);
                }
            }else{
                info(type + "请求异常");
                result.put(Constants.CODE_REMARK, "请求异常");
            }
        }catch (Exception e) {
            error("handleResponse(HttpResponse response,String type)异常", e);
            result.put(Constants.CODE_REMARK, "处理返回结果时异常");
        }
        return result;
    }


    /**
     * 微博认证登陆
     * @param Cookie
     * @param account
     * @return
     */
    public void gewaraWeiboOauth(JSONObject Cookie,JSONObject account,JSONObject result){
        try{
            WeiboOauth weiboOauth = WeiboOauth.getWeiboOauth();
            weiboOauth.Authorize(Cookie, account,result);
            if(!Cookie.isNull("code")){
                String url = Cookie.getString("code");
                JSONObject headers = new JSONObject();
                Request(null, headers, url);
                if(headers.getJSONObject("Cookie").isNull("gewara_uskey_")){//登陆成功
                    result.put(Constants.CODE_REMARK,"微博认证回调地址登陆失败，Cookie不存在");
                }else{
                    result.put("Cookie",headers.getJSONObject("Cookie"));
                }
            }
        }catch (Exception e){
            error("gewaraWeiboOauth(JSONObject Cookie,JSONObject account)异常",e);
        }
    }
}