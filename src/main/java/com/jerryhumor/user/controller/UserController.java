package com.jerryhumor.user.controller;

import com.jerryhumor.user.model.User;
import com.jerryhumor.util.JsonBuilder;
import com.jfinal.core.Controller;

import java.util.Date;
import java.util.List;

public class UserController extends Controller{

    private static String DEFAULT_FAVOUR = "1-1-1-1-1-1-1-1-1-1";

    public void login(){
        boolean isParamCorrect = true;
        User user = null;
        String account = getPara(0);
        String password = getPara(1);
        System.out.println("login, account: " + account + " password: " + password);
        if (account == null || password == null){
            isParamCorrect = false;
        }

        if (!isParamCorrect){
            System.out.println("登录失败");
            renderJson(generateErrorJson("参数错误"));
        }else{
            List<User> userList = User.dao.find("select * from user where account = '" + account + "'");
            if (userList != null && userList.size() > 0){
                user = userList.get(0);
               if (password.equals(user.getPassword())){
                   renderJson(JsonBuilder.generateUserJson(user));
               }else {
                   renderJson(generateErrorJson("账户名或密码错误"));
               }
            }else{
                renderJson(generateErrorJson("账户名或密码错误"));
            }
        }
    }

    public void register(){
        boolean isParamCorrect = true;
        String account = getPara(0);
        String password = getPara(1);
        String userName = getPara(2);
        System.out.println("login, account: " + account + " password: " + password + " user name: " + userName);
        if (account == null || password == null || userName == null){
            isParamCorrect = false;
        }
        if (!isParamCorrect){
            System.out.println("注册失败");
            renderJson(generateErrorJson("参数错误"));
        }else{
            List<User> userList = User.dao.find("select * from user where account = '" + account + "'");
            if (userList != null && userList.size() > 0){
                renderJson(generateErrorJson("账户已存在"));
            }else{
                User user = new User()
                        .setAccount(account)
                        .setPassword(password)
                        .setName(userName)
                        .setUniqueId(generateUniqueId())
                        .setFavour(DEFAULT_FAVOUR)
                        .setCreateTime(new Date());
                user.save();
                renderJson(JsonBuilder.generateUserJson(user));
            }
        }



    }

    private String generateErrorJson(String error){
        return "{\"status\":\"failed\", \"error\":\"" + error + "\"}";
    }

    private String generateUniqueId(){
        //todo 生成用户唯一的id
        String id = new Date().getTime() + "";
        return id;
    }

}
