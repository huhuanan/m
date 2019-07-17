package m.system.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import m.system.RuntimeData;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;


public class FileUtil {
	public static boolean existsWebFile(String url_path){
		File file=new File(RuntimeData.getWebPath()+url_path);
		return file.exists();
	}
	public static File getFile(String path) throws IOException{
		File file=new File(path);
		if(!file.exists()){
			getDirs(file.getPath().substring(0, file.getPath().lastIndexOf(file.getName())));
			file.createNewFile();
		}
		return file;
	}
	public static File getDirs(String path){
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return file;
	}
	public static File getWebFile(String url_path) throws IOException{
		return getFile(RuntimeData.getWebPath()+url_path);
	}
	public static void writeFile(String filePath,InputStream inStream) throws Exception{
		Exception e1=null;
		OutputStream outSteam=null;
		try {
			outSteam = new FileOutputStream(getFile(filePath));
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e1=e;
		} finally {
			try {
				outSteam.close();
			} catch (Exception e) {}
			try {
				inStream.close();
			} catch (Exception e) {}
		}
		if(null!=e1) throw e1;
	}
	public static void writeWebFile(String url_path,InputStream inStream) throws Exception{
		writeFile(RuntimeData.getWebPath()+url_path,inStream);
	}
	public static void writeFile(String filePath,File file) throws Exception {
		writeFile(filePath,new FileInputStream(file.getPath()));
	}
	public static void writeWebFile(String url_path,File file) throws Exception{
		writeFile(RuntimeData.getWebPath()+url_path,file);
	}
	public static String read(File file) throws IOException{
		StringBuilder result = new StringBuilder();
		InputStreamReader read = new InputStreamReader(new FileInputStream(file.getPath()), "UTF-8");
		BufferedReader br = new BufferedReader(read);
		String s = null;
		while ((s = br.readLine()) != null) {
			result.append(System.lineSeparator() + s);
		}
		br.close();
		return result.toString();
	}
	public static boolean deleteFile(String path) throws IOException{
		File file=getFile(path);
		if(file.isFile() && file.exists()){
			return file.delete();
		}else{
			return false;
		}
	}
	public static boolean deleteWebFile(String url_path) throws IOException{
		File file=getFile(RuntimeData.getWebPath()+url_path);
		if(file.isFile() && file.exists()){
			return file.delete();
		}else{
			return false;
		}
	}
	/**
	 * 保存缩略图
	 * @param file 源文件
	 * @param url_path 根目录开始的url
	 * @throws IOException
	 */
	public static void saveThumFile(File file,String url_path) throws IOException{
		BufferedImage bi=ImageIO.read(file);
		int n=bi.getHeight()>bi.getWidth()?bi.getWidth():bi.getHeight();
		File f=getFile(new StringBuffer(RuntimeData.getWebPath()).append(url_path).toString());
		if(url_path.toLowerCase().indexOf(".jpg")==url_path.length()-4){
			Thumbnails.of(file).sourceRegion(Positions.CENTER,n,n).size(300,300).outputFormat("JPEG").toFile(f);
		}else{
			Thumbnails.of(file).sourceRegion(Positions.CENTER,n,n).size(300,300).toFile(f);
		}
	}
	public static void saveThumFile(File file,String url_path,int width) throws IOException{
		BufferedImage bi=ImageIO.read(file);
		int n=bi.getHeight()>bi.getWidth()?bi.getWidth():bi.getHeight();
		File f=getFile(new StringBuffer(RuntimeData.getWebPath()).append(url_path).toString());
		if(url_path.toLowerCase().indexOf(".jpg")==url_path.length()-4){
			Thumbnails.of(file).sourceRegion(Positions.CENTER,n,n).size(width,width).outputFormat("JPEG").toFile(f);
		}else{
			Thumbnails.of(file).sourceRegion(Positions.CENTER,n,n).size(width,width).toFile(f);
		}
	}
	public static void saveThumFile(File file,String url_path,int width,int height) throws IOException{
		BufferedImage bi=ImageIO.read(file);
		double n=bi.getHeight()/new Double(height)<bi.getWidth()/new Double(width)?bi.getHeight()/new Double(height):bi.getWidth()/new Double(width);
		int w=new Double(Math.ceil(bi.getWidth()/n)).intValue(),h=new Double(Math.ceil(bi.getHeight()/n)).intValue();
		if(w<width) w=width;
		if(h<height) h=height;
		Thumbnails.of(file).size(w, h).sourceRegion(Positions.CENTER, bi.getWidth(), bi.getHeight()).toFile(file);
		File f=getFile(new StringBuffer(RuntimeData.getWebPath()).append(url_path).toString());
		if(url_path.toLowerCase().indexOf(".jpg")==url_path.length()-4){
			Thumbnails.of(file).size(width, height).sourceRegion(Positions.CENTER, width, height).outputFormat("JPEG").toFile(f);
		}else{
			Thumbnails.of(file).size(width, height).sourceRegion(Positions.CENTER, width, height).toFile(f);
		}
	}
	/**
	 * 保存显示图
	 * @param file 源文件
	 * @param url_path 根目录开始的url
	 * @throws IOException
	 */
	public static void saveImgFile(File file,String url_path) throws IOException{
		File f=getFile(new StringBuffer(RuntimeData.getWebPath()).append(url_path).toString());
		if(url_path.toLowerCase().indexOf(".jpg")==url_path.length()-4){
			Thumbnails.of(file).size(1600,1600).outputFormat("JPEG").toFile(f);
		}else{
			Thumbnails.of(file).size(1600,1600).toFile(f);
		}
	}
	/**
	 * 保存分类文件
	 * @param file 源文件
	 * @param url_path 根目录开始的url
	 * @throws IOException
	 */
	public static void saveFile(File file,String url_path,int maxWidth) throws IOException{
		File f=getFile(new StringBuffer(RuntimeData.getWebPath()).append(url_path).toString());
		if(url_path.toLowerCase().indexOf(".jpg")==url_path.length()-4){
			Thumbnails.of(file).size(maxWidth,maxWidth).outputFormat("JPEG").toFile(f);
		}else{
			Thumbnails.of(file).size(maxWidth,maxWidth).toFile(f);
		}
	}
	/**
	 * 保存字符到二维码文件
	 * @param text
	 * @param path
	 * @param width
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void saveQRCode(String text,String url_path,int width) throws WriterException, IOException{
		saveQRCode4Path(text, new StringBuffer(RuntimeData.getWebPath()).append(url_path).toString(), width);
	}
	public static void saveQRCode4Path(String text,String path,int width) throws WriterException, IOException{
		ImageIO.write(toQRCode(text, width), "png",getFile(path));
	}
	public static BufferedImage toQRCode(String text,int width) throws WriterException{
		Hashtable<EncodeHintType, Object> hints= new Hashtable<EncodeHintType, Object>(); 
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,width,width,hints); 
		BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < width; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		return image;
	}
	public static File getNetworkTempFile(String urlPath,String suffix){
		URL url=null;
		try {
			url = new URL(urlPath);
			File f=FileUtil.getFile(new StringBuffer(RuntimeData.getWebPath()).append(RuntimeData.getFilePath()).append("tmp/").append(GenerateID.tempKey()).append(".").append(suffix).toString());
			writeFile(f.getPath(), url.openStream());
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Map<String,String> getExifInfo(File file) {
		Map<String,String> exifMap=new HashMap<String,String>();
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(file);
			Collection<ExifSubIFDDirectory> exif=metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);
			if(null!=exif){
				Iterator<ExifSubIFDDirectory> exifs = exif.iterator();
				while(exifs.hasNext()){
					Iterator<Tag> tags = exifs.next().getTags().iterator();
					while (tags.hasNext()) {
						Tag tag = tags.next();
						//System.out.println(tag+"-----"+tag.getTagName()+":"+tag.getDescription());
						exifMap.put(tag.getTagName(), tag.getDescription());
					}
			 	}
			}
		} catch (JpegProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		}
		return exifMap;
	}
	
	public static void main(String[] a){
		try {
			saveQRCode4Path("http://xiaomao.fulicat.cn","d:/1.jpg",300);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
