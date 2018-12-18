package com.bquan.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.bquan.entity.mysql.Product;
import com.bquan.entity.mysql.Tips;

/**
 * user
 */
 @SuppressWarnings({ "unchecked", "rawtypes" })
public class UserBean  implements Serializable{
	/**
	 * id
	 */
	public String id;
	/**
	 * username
	 */
	public String username;

	/**
	 * time_left
	 */
	public int time_left;
	/**
	 * phone
	 */
	public String phone;
	/**
	 * status
	 */
	public String status;

	public String createDate;
	
	public String  buyDate ;

	public String token ;
	
	public String level;
	
	private String time_now;
	
	public String vip_end_time;
	
	public String share_code;
	
	public Integer left_count;
	
	public UserLineBean UserLineModel;
	
	public String free_start_time;
	
	public List<UserLineBean> userLineList;			// 系统线路信息
	
	public List<Product> productList;				// 产品列表
	
	public List<Tips> tipsList;					// tips
	
	public List<String> warmList;						// warm信息
	
	public List<UserCouponBean> userCouponList;			// 用户红包信息
	
	public String commissionMoney="";						// 佣金
	
	public String commissionCount="";						// 邀请人数
	
	public String qq;									// qq号
	
	public String depname;   //机构部门
	

    public String getDepname() {
		return depname;
	}

	public void setDepname(String depname) {
		this.depname = depname;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTime_left() {
        return time_left;
    }

    public void setTime_left(int time_left) {
        this.time_left = time_left;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getVip_end_time() {
		return vip_end_time;
	}

	public void setVip_end_time(String vip_end_time) {
		this.vip_end_time = vip_end_time;
	}

	public String getTime_now() {
		return time_now;
	}

	public void setTime_now(String time_now) {
		this.time_now = time_now;
	}

	public String getShare_code() {
		return share_code;
	}

	public void setShare_code(String share_code) {
		this.share_code = share_code;
	}

	public Integer getLeft_count() {
		return left_count;
	}

	public void setLeft_count(Integer left_count) {
		this.left_count = left_count;
	}

	

	public String getFree_start_time() {
		return free_start_time;
	}

	public void setFree_start_time(String free_start_time) {
		this.free_start_time = free_start_time;
	}

	/**
	 * @return the userLineModel
	 */
	public UserLineBean getUserLineModel() {
		return UserLineModel;
	}

	/**
	 * @param userLineModel the userLineModel to set
	 */
	public void setUserLineModel(UserLineBean userLineModel) {
		UserLineModel = userLineModel;
	}

	/**
	 * @return the userLineList
	 */
	public List<UserLineBean> getUserLineList() {
		return userLineList;
	}

	/**
	 * @param userLineList the userLineList to set
	 */
	public void setUserLineList(List<UserLineBean> userLineList) {
		this.userLineList = userLineList;
	}

	/**
	 * @return the productList
	 */
	public List<Product> getProductList() {
		return productList;
	}

	/**
	 * @param productList the productList to set
	 */
	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	/**
	 * @return the tipsList
	 */
	public List<Tips> getTipsList() {
		return tipsList;
	}

	/**
	 * @param tipsList the tipsList to set
	 */
	public void setTipsList(List<Tips> tipsList) {
		this.tipsList = tipsList;
	}

	public List<String> getWarmList() {
		return warmList;
	}

	public void setWarmList(List<String> warmList) {
		this.warmList = warmList;
	}

	public List<UserCouponBean> getUserCouponList() {
		return userCouponList;
	}

	public void setUserCouponList(List<UserCouponBean> userCouponList) {
		this.userCouponList = userCouponList;
	}

	public String getCommissionMoney() {
		return commissionMoney;
	}

	public void setCommissionMoney(String commissionMoney) {
		this.commissionMoney = commissionMoney;
	}

	public String getCommissionCount() {
		return commissionCount;
	}

	public void setCommissionCount(String commissionCount) {
		this.commissionCount = commissionCount;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

}
