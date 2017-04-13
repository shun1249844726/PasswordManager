package com.lexinsmart.xushun.passwordmanager.modle;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by xushun on 2017/4/10.
 */

public class PasswordBean extends BmobObject {

    /**
     * password :
     * psdname :
     * imgsource :
     * user :
     */

    private String password;
    private String psdname;
    private String imgsource;
    private BmobUser user;
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPsdname() {
        return psdname;
    }

    public void setPsdname(String psdname) {
        this.psdname = psdname;
    }

    public String getImgsource() {
        return imgsource;
    }

    public void setImgsource(String imgsource) {
        this.imgsource = imgsource;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
