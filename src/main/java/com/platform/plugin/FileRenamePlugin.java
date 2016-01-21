package com.platform.plugin;

import com.jfinal.plugin.IPlugin;
import com.jfinal.upload.OreillyCos;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import com.platform.tools.ToolRandoms;

import java.io.File;

/**
 * 配置文件上传命名策略
 */
public class FileRenamePlugin implements IPlugin {

	@Override
	public boolean start() {
		OreillyCos.setFileRenamePolicy(new FileRenamePolicy() {
			@Override
			public File rename(File file) {
				String ext = "";   
		        int pot = file.getName().lastIndexOf(".");   
		        if(pot != -1){   
		        	ext = file.getName().substring(pot);   
		        }else{   
		        	ext = "";   
		        }   
		        String newName = ToolRandoms.getUuid(true) + ext;   
		        file = new File(file.getParent(),newName);   
		        return file;
			}
		});
		
		return true;
	}

	@Override
	public boolean stop() {
		return false;
	}
	
}
