package manage.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Random;

import m.system.cache.CacheUtil;
import m.system.exception.MException;
import m.system.util.StringUtil;

public class CaptchaUtil {
	public static void setMastVerify(String cacheKey) {
		CacheUtil.push(cacheKey+"_verify", true);
	}
	public static void clearMastVerify(String cacheKey) {
		CacheUtil.clear(cacheKey+"_verify");
	}
	public static void clearCode(String cacheKey) {
		CacheUtil.clear(cacheKey+"_code");
	}
	public static boolean isMastVerify(String cacheKey) {
		if(null!=CacheUtil.get(cacheKey+"_verify")) {
			return (Boolean) CacheUtil.get(cacheKey+"_verify");
		}else {
			return false;
		}
	}
	public static void verifyCaptcha(String cacheKey,String code) throws MException {
		Object captcha=CacheUtil.get(cacheKey+"_code");
		if(null!=CacheUtil.get(cacheKey+"_verify")) {
			if(StringUtil.isSpace(code)) {
				throw new MException(CaptchaUtil.class, "请输入验证码");
			}else if(null==captcha){
				throw new MException(CaptchaUtil.class, "验证码已失效");
			}else if(!code.toLowerCase().equals(captcha.toString())) {
				throw new MException(CaptchaUtil.class, "验证码错误");
			}
		}
	}
	
	private static char mapTable[] = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0', '1',
            '2', '3', '4', '5', '6', '7',
            '8', '9'};
    public static BufferedImage getImageCode(String cacheKey,OutputStream os) {
    	int width=60,height=22;
        if (width <= 0) width = 60;
        if (height <= 0) height = 22;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();
        //生成随机类
        Random random = new Random();
        // 设定背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        //设定字体
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        // 随机产生168条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 168; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        //取随机产生的码
        String code = "";
        //4代表4位验证码,如果要生成更多位的认证码,则加大数值
        for (int i = 0; i < 4; ++i) {
        	code += mapTable[(int) (mapTable.length * Math.random())];
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            // 直接生成
            String str = code.substring(i, i + 1);
            // 设置随便码在背景图图片上的位置
            g.drawString(str, 13 * i + 7, height-13/2);
        }
        // 释放图形上下文
        g.dispose();
        CacheUtil.push(cacheKey+"_code",code.toLowerCase());
        return image;
    }
    //给定范围获得随机颜色
    static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
