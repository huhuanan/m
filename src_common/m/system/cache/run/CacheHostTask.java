package m.system.cache.run;

import java.util.Date;

import m.system.SystemTaskRun;
import m.system.cache.CacheHost;
import m.system.cache.CacheUtil;
import m.system.util.DateUtil;

public class CacheHostTask extends SystemTaskRun {

	@Override
	public void run(boolean isMain) {
		if(isMain) {
			int s=Integer.parseInt(DateUtil.format(new Date(), "ss"));
			if(s%10==0) {
				String[] ls=CacheHost.getTimeoutCaches(20*60*1000);
				for(String key : ls) {
					CacheUtil.clear(key,false);
				}
			}
		}else {
			String[] ls=CacheHost.getTimeoutCaches(1*60*1000);
			for(String key : ls) {
				CacheUtil.clear(key,false);
			}
		}
	}

}
