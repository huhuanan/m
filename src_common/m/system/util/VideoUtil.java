package m.system.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.DefaultFFMPEGLocator;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.VideoAttributes;
import it.sauronsoftware.jave.VideoSize;


public class VideoUtil extends DefaultFFMPEGLocator {
	private static VideoUtil util=null;
	private static VideoUtil getSelf(){
		if(null==util){
			util=new VideoUtil();
		}
		return util;
	}
	/**
	 * 压缩视频 并保存缩略图
	 * @param upFilePath
	 * @param targetPath
	 * @throws IllegalArgumentException
	 * @throws InputFormatException
	 * @throws EncoderException
	 * @throws IOException 
	 */
	public static void zipVideo(File upFile,String targetPath,String mediaPicPath) throws IllegalArgumentException, InputFormatException, EncoderException, IOException{
		File pic=FileUtil.getWebFile(mediaPicPath);
		File target = FileUtil.getWebFile(targetPath);
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(new Integer(64000));
		audio.setChannels(new Integer(1));
		audio.setSamplingRate(new Integer(22050));
		VideoAttributes video = new VideoAttributes();
		video.setCodec("mpeg4");
		video.setBitRate(new Integer(180000));
		video.setFrameRate(new Integer(15));
		video.setSize(new VideoSize(768, 432));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp4");
		attrs.setAudioAttributes(audio);
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		encoder.encode(upFile, target, attrs);
        // 创建一个List集合来保存从视频中截取图片的命令
        List<String> cutpic = new ArrayList<String>();
        cutpic.add(getSelf().getFFMPEGExecutablePath());
        cutpic.add("-i");
        cutpic.add(target.getPath()); // 同上（指定的文件即可以是转换为flv格式之前的文件，也可以是转换的flv文件）
        cutpic.add("-y");
        cutpic.add("-f");
        cutpic.add("image2");
        cutpic.add("-ss"); // 添加参数＂-ss＂，该参数指定截取的起始时间
        cutpic.add("1"); // 添加起始时间为第1秒
        cutpic.add("-t"); // 添加参数＂-t＂，该参数指定持续时间
        cutpic.add("0.001"); // 添加持续时间为1毫秒
        cutpic.add("-s"); // 添加参数＂-s＂，该参数指定截取的图片大小
        cutpic.add("768*432"); // 添加截取的图片大小为512*288
        cutpic.add(pic.getPath()); // 添加截取的图片的保存路径

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cutpic);
        builder.redirectErrorStream(true);
        // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
        //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
//        builder.start();
        Process process = builder.start();
        InputStream in = process.getInputStream();
        byte[] re = new byte[1024];
        while (in.read(re)!= -1) {
            //System.out.println(new String(re));
        }
	}
}
