package com.bquan.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.beanutils.BeanUtils;

import com.bquan.bean.MimeMessageDTO;


/**
 * 类名称:  mailUtil
 * 功能描述: TODO 邮件发送例子
 * 创建人:  Gavin-Nie 
 * 创建时间: 2014-12-4 上午9:20:16 
 * @version  V1.0  
 */
public class MailUtil {
	
	/**   
	 * 变量名 userName: TODO 邮箱用户名
	 */   
	private String userName;
	
	/**   
	 * 变量名 password: TODO 邮箱地址
	 */   
	private String password;
	
	/**   
	 * 变量名 smtpHost: TODO 邮箱smtp地址，发送地址
	 */   
	private String smtpHost;
	
	/**   
	 * 变量名 targetAddress: TODO 目标邮箱地址
	 */   
	private String targetAddress;
	
	/** 
	 * 方法名: sendEmail 
	 * 功能描述: TODO 发送邮件
	 * @param: @param userName 邮箱账号
	 * @param: @param password 邮箱密码
	 * @param: @param targetAddress 目标邮箱地址
	 * @param: @param mimeDTO 邮件部分参数
	 * @return: boolean 
	 */
	public static boolean sendEmail(
			String userName,String password,String targetAddress,MimeMessageDTO mimeDTO){
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.163.com");
		props.setProperty("mail.smtp.auth", "true");
		props.put("mail.smtp.user", userName);
		props.put("mail.smtp.password", password);
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.socketFactory.port", "994");
//		props.put("mail.smtp.port", "994");
		Session session = Session.getInstance(props, new PopupAuthenticator(userName, password));
		
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(userName));
			msg.setRecipients(Message.RecipientType.TO, targetAddress);
			//把DTO设置的内容，复制到msg中
			BeanUtils.copyProperties(msg, mimeDTO);
			//发送邮件
			Transport.send(msg);
			
		} catch (Exception mex) {
			System.out.println(mex.getMessage());
			System.out.println(userName+"账户发送失败！");
			return false;
		}
		return true;
	}
	
	public static boolean sendMail(String title,String content,String emailName){
		
		String targetAddress = null;
		MimeMessageDTO mimeDTO = null;
		
		//设置邮件内容
		mimeDTO = new MimeMessageDTO();
		mimeDTO.setSentDate(new Date());
		mimeDTO.setSubject(title);
		mimeDTO.setText(content);
		targetAddress = emailName;
		
		// 发送标志
		boolean sendFlag = false;
		
		try {
			//发送邮件
			sendFlag = MailUtil.sendEmail(
					"tianyantongvip@163.com", "lxk17786505186", targetAddress, mimeDTO);
		} catch (Exception e) {
			sendFlag = false;
		}
		
		// 发送失败，切换邮箱发送
//		if(!sendFlag){
//			try {
//				//发送邮件
//				sendFlag = MailUtil.sendEmail(
//						"254651426@qq.com", "sqkhuuqcgyckbigh", targetAddress, mimeDTO);
//			} catch (Exception e) {
//				sendFlag = false;
//			}
//		}
		
		// 发送失败，切换邮箱发送
//		if(!sendFlag){
//			try {
//				//发送邮件
//				sendFlag = MailUtil.sendEmail(
//						"cworld@vip.126.com", "uzdodaojmgxssgdn", targetAddress, mimeDTO);
//			} catch (Exception e) {
//				sendFlag = false;
//			}
//		}
		return sendFlag;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public String getTargetAddress() {
		return targetAddress;
	}
	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}
	
	public static void main(String[] args) {
		System.out.println(MailUtil.sendMail("影盾", "验证码：1234567", "254651426@qq.com"));
	}
}
